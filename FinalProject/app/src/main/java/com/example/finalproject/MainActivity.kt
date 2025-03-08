package com.example.finalproject

import android.app.ActionBar
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toolbar
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.room.Room
import com.example.finalproject.data.AppDatabase

import com.example.finalproject.ui.theme.FinalProjectTheme
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.example.finalproject.data.Video
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.finalproject.data.VideoViewModel
import com.example.finalproject.data.VideoViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashscreen = installSplashScreen()
        var keepSplashScreen = true
        super.onCreate(savedInstanceState)

        // Video Database Setup
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "project_database"
        ).build()
        val videoDao = db.videoDao()
        val videoViewModel: VideoViewModel by viewModels {
            VideoViewModelFactory(videoDao)
        }

        splashscreen.setKeepOnScreenCondition { keepSplashScreen }
        lifecycleScope.launch {
            delay(5000)
            keepSplashScreen = false
        }

        enableEdgeToEdge()
        setContent {
            FinalProjectTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text("Video Library")
                            }
                        )
                    }
                ) { innerPadding ->
                    Column(modifier = Modifier.padding(all = 10.dp).padding(innerPadding)) {
                        VideoList(videoViewModel)
                        Spacer(modifier = Modifier.padding(10.dp))
                        VideoField(videoViewModel)
                    }
                }

            }
        }
    }
}

@Composable
fun TitleBar() {
    Text("Videos")
}

@Composable
fun VideoField(videoViewModel: VideoViewModel) {
    val coroutineScope = rememberCoroutineScope()
    var titleText by remember { mutableStateOf("")}
    var linkText by remember { mutableStateOf("")}

    Text(
        "New Video",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )
    TextField(
        value = titleText,
        onValueChange = {newText -> titleText = newText},
        label = { Text("Video Title") }
    )
    TextField(
        value = linkText,
        onValueChange = {newText -> linkText = newText},
        label = { Text("Video Link") }
    )

    Button(onClick = {
        if( titleText.isNotBlank() and linkText.isNotBlank() ) {
            coroutineScope.launch {
                val newTitle = titleText
                val newLink = linkText
                titleText = ""
                linkText = ""
                videoViewModel.addVideo(Video(title = newTitle, link = newLink))
            }
        }
    }) {Text("Add Video")}
}

@Composable
fun VideoList(videoViewModel: VideoViewModel) {
    val videos by videoViewModel.videos.collectAsState()
    var playerLink by remember { mutableStateOf(Video(0,"",""))}

    VideoPlayer(playerLink.link.toString())

    Text(
        playerLink.title.toString(),
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.padding(10.dp))

    Text(
        "Videos",
        style = MaterialTheme.typography.titleLarge,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Bold
    )

    LazyColumn {
        items(videos) { video ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${video.title}",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f).padding(end = 8.dp),
                    maxLines = 2
                )
                Button(
                    onClick = { playerLink = video },
                ) { Text("Play") }
            }
        }
    }
}

@Composable
fun VideoPlayer(videoId: String) {
    val context = LocalContext.current
    val youTubePlayerView = remember { YouTubePlayerView(context) }
    var youTubePlayerInstance by remember { mutableStateOf<YouTubePlayer?>(null) }

    AndroidView(factory = {
        youTubePlayerView.apply {
            addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayerInstance = youTubePlayer
                    youTubePlayer.loadVideo(videoId, 0f)
                }
            })
        }
    })

    LaunchedEffect(videoId) {
        youTubePlayerInstance?.loadVideo(videoId, 0f)
    }
}