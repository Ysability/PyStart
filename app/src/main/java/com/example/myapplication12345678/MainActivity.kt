package com.example.myapplication12345678

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import com.example.myapplication12345678.ui.auth.AuthRoot
import com.example.myapplication12345678.ui.theme.MyApplication12345678Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplication12345678Theme {
                AuthRoot()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AuthPreview() {
    MyApplication12345678Theme {
        AuthRoot()
    }
}