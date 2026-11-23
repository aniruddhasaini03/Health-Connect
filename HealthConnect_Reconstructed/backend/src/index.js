import { createRemoteJWKSet, jwtVerify } from "jose";

const JWKS = createRemoteJWKSet(
  new URL("https://www.googleapis.com/service_accounts/v1/jwk/securetoken@system.gserviceaccount.com")
);

const urgentTerms = [
  "chest pain", "can't breathe", "cannot breathe", "difficulty breathing",
  "severe bleeding", "unconscious", "suicide", "overdose", "stroke", "heart attack"
];

function json(data, status = 200) {
  return new Response(JSON.stringify(data), {
    status,
    headers: { "content-type": "application/json; charset=utf-8" }
  });
}

async function verifyFirebaseToken(request, env) {
  const auth = request.headers.get("authorization") || "";
  if (!auth.startsWith("Bearer ")) throw new Error("Missing bearer token");
  const token = auth.slice(7);
  const issuer = `https://securetoken.google.com/${env.FIREBASE_PROJECT_ID}`;
  const { payload } = await jwtVerify(token, JWKS, {
    issuer,
    audience: env.FIREBASE_PROJECT_ID
  });
  return payload;
}

function buildPrompt(body) {
  const context = body.userContext || {};
  const profile = [
    context.age != null ? `Age: ${context.age}` : null,
    context.gender ? `Gender: ${context.gender}` : null,
    context.heightCm != null ? `Height: ${context.heightCm} cm` : null,
    context.weightKg != null ? `Weight: ${context.weightKg} kg` : null,
    context.healthGoals?.length ? `Health goals: ${context.healthGoals.join(", ")}` : null
  ].filter(Boolean).join("\n");

  const history = (body.messages || []).slice(-20)
    .map(m => `${m.role === "assistant" ? "Assistant" : "User"}: ${String(m.content || "")}`)
    .join("\n");

  return `You are HealthConnect's AI health assistant. Give clear, cautious, non-diagnostic health information.
Do not claim to be a doctor. Do not invent test results. Encourage professional care when appropriate.
For possible emergencies, instruct the user to contact local emergency services immediately.
Keep the answer practical and concise.

USER PROFILE (may be incomplete):
${profile || "No profile data available"}

CONVERSATION:
${history}`;
}

async function callGemini(prompt, env) {
  if (!env.GEMINI_API_KEY) throw new Error("GEMINI_API_KEY is not configured");
  const model = env.GEMINI_MODEL || "gemini-1.5-flash";
  const url = `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(model)}:generateContent?key=${encodeURIComponent(env.GEMINI_API_KEY)}`;
  const response = await fetch(url, {
    method: "POST",
    headers: { "content-type": "application/json" },
    body: JSON.stringify({
      contents: [{ role: "user", parts: [{ text: prompt }] }],
      generationConfig: { temperature: 0.3, maxOutputTokens: 700 }
    })
  });
  if (!response.ok) throw new Error(`Gemini error ${response.status}`);
  const data = await response.json();
  return data?.candidates?.[0]?.content?.parts?.map(p => p.text || "").join("").trim() || "";
}

export default {
  async fetch(request, env) {
    const url = new URL(request.url);
    if (request.method === "GET" && url.pathname === "/") {
      return json({ ok: true, service: "HealthConnect backend" });
    }
    if (request.method !== "POST" || url.pathname !== "/chat") {
      return json({ error: "Not found" }, 404);
    }

    try {
      await verifyFirebaseToken(request, env);
      const body = await request.json();
      const lastText = String(body?.messages?.at?.(-1)?.content || "").toLowerCase();
      if (urgentTerms.some(term => lastText.includes(term))) {
        return json({
          reply: "This may be a medical emergency. Please call your local emergency number or go to the nearest emergency department immediately.",
          isUrgentWarning: true
        });
      }
      const reply = await callGemini(buildPrompt(body), env);
      return json({ reply: reply || "I couldn't generate a response right now. Please try again.", isUrgentWarning: false });
    } catch (error) {
      return json({ error: error?.message || "Request failed" }, 401);
    }
  }
};
