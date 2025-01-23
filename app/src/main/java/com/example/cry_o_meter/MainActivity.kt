package com.example.cry_o_meter

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cry_o_meter.ui.theme.CryometerTheme
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CryometerTheme {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                titleContentColor = MaterialTheme.colorScheme.primary,
                            ),
                            title = {
                                Text(stringResource(R.string.app_name))
                            }
                        )
                    },
                ) { innerPadding ->
                    Cryometer(
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun Cryometer(modifier: Modifier = Modifier) {
    //tilat itkun ajalle ja aloitusajalle sekä isCrying booleanille
    //(määritellään napin teksti), myös "itkulistaa" varten tilat alkuajoille ja
    //lista itkuille, joka näytetään nappien alla
    var isCrying by remember { mutableStateOf(false) }
    var cryTime by remember { mutableStateOf(0L) }
    var startTime by remember { mutableStateOf<Long?>(null) }
    //lista on Pair-lista, sisältäen kaksi Long-tyyppiä (startTime ja cryTime)
    var savedCries by remember { mutableStateOf<List<Pair<Long, Long>>>(emptyList()) }

    //Lasketaan kulunut aika
    LaunchedEffect(isCrying) {
        //käynnistetään loop vain kun itkee
        if (isCrying) {
            //loopissa päivitetään kulunut aika 10ms välein
            while (isCrying) {
                delay(1000)
                cryTime += 1000
            }
        }
    }
    //layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        //ajannäyttö
        Text(text = formatCryTime(cryTime), fontSize = 25.sp)
        Spacer(modifier = Modifier.height(16.dp))
        //Napit
        Row {
            //teksti muuttuu isCrying tilan mukaan
            Button(
                onClick = {
                    isCrying = !isCrying
                    //tallennetaan alkuaika
                    if (isCrying) {
                        startTime = System.currentTimeMillis()
                    //tallennetaan alkuaika ja itkun kesto listaan
                    } else {
                        //varmistetaan ettei ole null
                        if (startTime != null) {
                            savedCries = savedCries + Pair(startTime!!, cryTime)
                            cryTime = 0L
                        }
                    }
                }
            ) {
                Text(if (isCrying) "Stop Cry" else "Start Cry")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        //tallennetut itkut
        SavedCries(savedCries)
    }
}

@Composable
fun SavedCries(savedCries: List<Pair<Long,Long>>) {
    LazyColumn {
        items(savedCries.size) { index ->
            //tallennetaan parin tiedot omiin muuttujiinsa
            val (startTime, cryTime) = savedCries[index]
            //formatoidaan starttime luettavaan muotoon sdf:n avulla
            val formattedStartTime = SimpleDateFormat("dd.MM 'klo' HH:mm", Locale.getDefault())
                .format(Date(startTime))
            //formatoidaan itkuaika funktion avulla
            val formattedCryTime = formatCryTime(cryTime)
            Text(text = "Cry ${index+1}: $formattedStartTime: $formattedCryTime")
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
//muokkaa ajan luettavaan muotoon -> sdf parempi??
fun formatCryTime(cryTime: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(cryTime)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(cryTime) % 60
    return String.format("%02d:%02d", minutes, seconds)
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CryometerTheme {
        Cryometer()
    }
}