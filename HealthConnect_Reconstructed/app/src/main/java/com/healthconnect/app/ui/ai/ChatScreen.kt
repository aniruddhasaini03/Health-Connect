package com.healthconnect.app.ui.ai

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.healthconnect.app.data.model.ChatMessage
import com.healthconnect.app.data.model.MessageSender
import com.healthconnect.app.viewmodel.ChatViewModel

@Composable
fun ChatScreen(vm:ChatViewModel=viewModel()){
 val s by vm.uiState.collectAsStateWithLifecycle();var text by remember{mutableStateOf("")};val list=rememberLazyListState();LaunchedEffect(Unit){vm.loadHistory()};LaunchedEffect(s.messages.size){if(s.messages.isNotEmpty())list.animateScrollToItem(s.messages.lastIndex)}
 Scaffold(topBar={TopAppBar(title={Text("AI Health Assistant")},actions={TextButton(onClick={vm.clearConversation()}){Text("Clear conversation")}})},bottomBar={Row(Modifier.fillMaxWidth().padding(8.dp),verticalAlignment=Alignment.CenterVertically){OutlinedTextField(text,{text=it},placeholder={Text("Ask a health question...")},modifier=Modifier.weight(1f));Spacer(Modifier.width(8.dp));Button(onClick={val t=text;text="";vm.sendMessage(t)},enabled=text.isNotBlank()&&!s.isSending){Text("Send")}}}){p->LazyColumn(state=list,modifier=Modifier.padding(p).fillMaxSize(),contentPadding=PaddingValues(12.dp),verticalArrangement=Arrangement.spacedBy(8.dp)){if(s.messages.isEmpty())item{Box(Modifier.fillParentMaxSize(),contentAlignment=Alignment.Center){Column(horizontalAlignment=Alignment.CenterHorizontally){Text("Start the conversation",style=MaterialTheme.typography.titleLarge);Text("Ask about symptoms, wellness tips, or your health goals.")}}}else items(s.messages,key={it.id+it.timestamp}){ChatBubble(it)};if(s.isSending)item{Text("Thinking...")};s.errorMessage?.let{item{Text(it,color=MaterialTheme.colorScheme.error)}}}}
}
@Composable private fun ChatBubble(m:ChatMessage){Row(Modifier.fillMaxWidth(),horizontalArrangement=if(m.sender==MessageSender.USER)Arrangement.End else Arrangement.Start){Card(colors=CardDefaults.cardColors(containerColor=if(m.isUrgentWarning)MaterialTheme.colorScheme.errorContainer else if(m.sender==MessageSender.USER)MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant),modifier=Modifier.widthIn(max=310.dp)){Column(Modifier.padding(12.dp)){if(m.isUrgentWarning)Text("Important",style=MaterialTheme.typography.labelLarge,color=MaterialTheme.colorScheme.error);Text(m.text)}}}}
