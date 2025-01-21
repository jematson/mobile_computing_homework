package com.example.homework1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.homework1.ui.theme.Homework1Theme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Surface
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.material3.Button
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MyAppNavHost(navHostController: NavHostController) {
    NavHost(
        navController = navHostController,
        startDestination = "mainScreen"
    ) {
        composable("mainScreen") {
            MainScreen(
                onNavigateToMessages = {navHostController.navigate("messageScreen")}
            )
        }
        composable("messageScreen") {
            Conversation(
                onNavigateBack = {navHostController.popBackStack("mainScreen", false)},
                messages = SampleData.conversationSample
            )
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Homework1Theme {
                val navController = rememberNavController()
                MyAppNavHost(navHostController = navController)
            }
        }
    }
}

data class Message(val author: String, val body: String)

@Composable
fun MainScreen(onNavigateToMessages: () -> Unit) {
    Column(modifier = Modifier.padding(top = 20.dp),) {
        Button(onClick = onNavigateToMessages) { Text("Messages") }
    }
}

@Composable
fun MessageCard(msg: Message) {
    Row(modifier = Modifier.padding(all = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.fish),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
        )
        Spacer(modifier = Modifier.width(8.dp))

        // We keep track if the message is expanded or not in this
        // variable
        var isExpanded by remember { mutableStateOf(false) }
        val surfaceColor by animateColorAsState(
            if (isExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        )

        // We toggle the isExpanded variable when we click on this Column
        Column(modifier = Modifier.clickable { isExpanded = !isExpanded }) {
            Text(
                text = msg.author,
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.titleSmall
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 1.dp,
                color = surfaceColor,
                modifier = Modifier.animateContentSize().padding(1.dp)
            ) {
                Text(
                    text = msg.body,
                    modifier = Modifier.padding(all = 4.dp),
                    // If the message is expanded, we display all its content
                    // otherwise we only display the first line
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
fun Conversation(onNavigateBack: () -> Unit, messages: List<Message>) {
    Column(modifier = Modifier.padding(top = 20.dp),) {
        Button(onNavigateBack) { Text("Home") }
        LazyColumn(userScrollEnabled = true) {
            items(messages) { message ->
                MessageCard(message)

            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMessageCard() {
    Homework1Theme {
        Surface {
            MessageCard(msg = Message("Person", "Wow look at this thing"))
        }
    }
}

@Preview
@Composable
fun PreviewConversation() {
    Homework1Theme {
        // Conversation(SampleData.conversationSample)
    }
}

/**
 * SampleData for Jetpack Compose Tutorial
 */
object SampleData {
    // Sample conversation data
    val conversationSample = listOf(
        Message(
            "Kirby",
            "Test...Test...Test..."
        ),
        Message(
            "Kirby",
            """List of Android versions:
            |Android KitKat (API 19)
            |Android Lollipop (API 21)
            |Android Marshmallow (API 23)
            |Android Nougat (API 24)
            |Android Oreo (API 26)
            |Android Pie (API 28)
            |Android 10 (API 29)
            |Android 11 (API 30)
            |Android 12 (API 31)""".trim()
        ),
        Message(
            "Kirby",
            """I think Kotlin is my favorite programming language.
            |It's so much fun!""".trim()
        ),
        Message(
            "Kirby",
            "Searching for alternatives to XML layouts..."
        ),
        Message(
            "Kirby",
            """Hey, take a look at Jetpack Compose, it's great!
            |It's the Android's modern toolkit for building native UI.
            |It simplifies and accelerates UI development on Android.
            |Less code, powerful tools, and intuitive Kotlin APIs :)""".trim()
        ),
        Message(
            "Kirby",
            "It's available from API 21+ :)"
        ),
        Message(
            "Kirby",
            "Writing Kotlin for UI seems so natural, Compose where have you been all my life?"
        ),
        Message(
            "Kirby",
            "Android Studio next version's name is Arctic Fox"
        ),
        Message(
            "Kirby",
            "Android Studio Arctic Fox tooling for Compose is top notch ^_^"
        ),
        Message(
            "Kirby",
            "I didn't know you can now run the emulator directly from Android Studio"
        ),
        Message(
            "Kirby",
            "Compose Previews are great to check quickly how a composable layout looks like"
        ),
        Message(
            "Kirby",
            "Previews are also interactive after enabling the experimental setting"
        ),
        Message(
            "Kirby",
            "Have you tried writing build.gradle with KTS?"
        ),
    )
}