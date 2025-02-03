package com.example.homework1

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.example.homework1.data.User
import com.example.homework1.data.UserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@Composable
fun Profile(userViewModel: UserViewModel, onNavigateBack: () -> Unit) {
    val context = LocalContext.current
    var imageFile = File(context.filesDir, "profile.jpg")
    val users by userViewModel.users.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val painter = rememberAsyncImagePainter(imageFile)

    var selectedImage by remember {
        mutableStateOf<Uri?>(null)
    }

    fun saveImageFile(context: Context, imageUri: Uri, file: File) {
        coroutineScope.launch(Dispatchers.IO) {
            context.contentResolver.openInputStream(imageUri)?.use { input ->
                FileOutputStream(file).use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("Profile", "Image saved successfully!")
        }
    }

    val pickMedia = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let {
                selectedImage = it
                saveImageFile(context, it, imageFile)
            }
        }
    )

    fun launchPhotoPicker() {
        pickMedia.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    Column (
        modifier = Modifier.padding(all = 20.dp).padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onNavigateBack) { Text("Home") }
        LazyColumn {
            items(users) { user ->
                Row(modifier = Modifier.padding(all = 8.dp)) {
                    Image(
                        painter = painter,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${user.userName}",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        }

        Button(onClick = {
            launchPhotoPicker()
        }) {
            Text("Profile Picture")
        }

        UsernameField(userViewModel)

        ImageLayoutView(selectedImage = selectedImage)
    }
}

@Composable
fun UsernameField(userViewModel: UserViewModel) {
    var text by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    TextField(
        value = text,
        onValueChange = {newText -> text = newText},
        label = { Text("Enter username") }
    )
    Button(onClick = {
        // add user to database
        if (text.isNotBlank()) {
            coroutineScope.launch {
                userViewModel.addUser(User(uid = 0, userName = text))
                text = ""
            }
        }
    }) { Text("Submit") }
}

@Composable
fun ImageLayoutView(selectedImage: Uri?) {
        AsyncImage(
            model = selectedImage,
            contentDescription = null,
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape),
            contentScale = ContentScale.Fit
        )
}