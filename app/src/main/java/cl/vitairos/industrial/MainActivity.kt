package cl.vitairos.industrial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import cl.vitairos.industrial.ui.navigation.AppNavHost
import cl.vitairos.industrial.ui.theme.VitairosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as VitairosApp
        enableEdgeToEdge()
        setContent {
            VitairosTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    AppNavHost(container = app.container, modifier = Modifier.safeDrawingPadding())
                }
            }
        }
    }
}
