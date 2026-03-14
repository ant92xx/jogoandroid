package com.exemplo.lutafeminina

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.exemplo.lutafeminina.ui.theme.LutaTheme
import kotlinx.coroutines.delay
import kotlin.math.max
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LutaTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    LutaGameApp()
                }
            }
        }
    }
}

enum class Outfit(val label: String) { BIKINI("Biquíni"), LINGERIE("Lingerie") }
enum class Scenario(val label: String) { CIDADE("Cidade"), PRAIA("Praia"), CASA("Casa") }

data class Fighter(
    val name: String,
    val age: Int,
    val style: String,
    val powerName: String,
    val punch: Int,
    val kick: Int,
    val specialDamage: Int,
    val color: Color
)

private val fighters = listOf(
    Fighter("Ana Clara", 18, "Capoeira Urbana", "Rajada de Vento", 8, 12, 30, Color(0xFFFF6F91)),
    Fighter("Beatriz Nogueira", 19, "Muay Thai", "Impacto Solar", 11, 10, 32, Color(0xFFFF9671)),
    Fighter("Camila Souza", 20, "Jiu-Jitsu", "Prisão de Energia", 9, 9, 36, Color(0xFFFFC75F)),
    Fighter("Dandara Lima", 18, "Taekwondo", "Chute Relâmpago", 7, 14, 31, Color(0xFF8BC34A)),
    Fighter("Estela Martins", 20, "Boxe Técnico", "Pulso Psíquico", 13, 8, 34, Color(0xFF4FC3F7))
)

@Composable
fun LutaGameApp() {
    var playerIndex by remember { mutableIntStateOf(0) }
    var enemyIndex by remember { mutableIntStateOf(1) }
    var scenario by remember { mutableStateOf(Scenario.CIDADE) }
    var outfit by remember { mutableStateOf(Outfit.BIKINI) }
    var battleStarted by remember { mutableStateOf(false) }

    if (!battleStarted) {
        SelectionScreen(
            playerIndex = playerIndex,
            enemyIndex = enemyIndex,
            scenario = scenario,
            outfit = outfit,
            onPlayerChange = { playerIndex = it },
            onEnemyChange = { enemyIndex = it },
            onScenarioChange = { scenario = it },
            onOutfitChange = { outfit = it },
            onStart = { battleStarted = true }
        )
    } else {
        BattleScreen(
            player = fighters[playerIndex],
            enemy = fighters[enemyIndex],
            scenario = scenario,
            outfit = outfit,
            onExit = { battleStarted = false }
        )
    }
}

@Composable
fun SelectionScreen(
    playerIndex: Int,
    enemyIndex: Int,
    scenario: Scenario,
    outfit: Outfit,
    onPlayerChange: (Int) -> Unit,
    onEnemyChange: (Int) -> Unit,
    onScenarioChange: (Scenario) -> Unit,
    onOutfitChange: (Outfit) -> Unit,
    onStart: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111111))
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Luta Feminina 2D", color = Color.White, style = MaterialTheme.typography.headlineSmall)
        Text("Escolha personagem, roupa e cenário", color = Color.LightGray)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            fighters.forEachIndexed { index, f ->
                FighterCard(f, selected = playerIndex == index, onClick = { onPlayerChange(index) })
            }
        }

        Text("Adversária", color = Color.White, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            fighters.filterIndexed { i, _ -> i != playerIndex }.forEach { f ->
                val idx = fighters.indexOf(f)
                FighterCard(f, selected = enemyIndex == idx, onClick = { onEnemyChange(idx) })
            }
        }

        Text("Roupa", color = Color.White, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Outfit.entries.forEach {
                ToggleChip(text = it.label, selected = outfit == it) { onOutfitChange(it) }
            }
        }

        Text("Cenário", color = Color.White, fontWeight = FontWeight.Bold)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Scenario.entries.forEach {
                ToggleChip(text = it.label, selected = scenario == it) { onScenarioChange(it) }
            }
        }

        Spacer(Modifier.height(8.dp))
        Button(onClick = onStart, enabled = playerIndex != enemyIndex) {
            Text("Iniciar Luta")
        }
    }
}

@Composable
fun FighterCard(fighter: Fighter, selected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .border(if (selected) 2.dp else 0.dp, Color.White, RoundedCornerShape(12.dp))
            .pointerInput(Unit) { detectTapGestures(onTap = { onClick() }) },
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F1F1F))
    ) {
        Column(Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(fighter.name, color = Color.White, fontWeight = FontWeight.Bold)
            Text("${fighter.age} anos", color = Color.LightGray)
            Text(fighter.style, color = fighter.color)
            Text("Poder: ${fighter.powerName}", color = Color.LightGray)
        }
    }
}

@Composable
fun ToggleChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(onClick = onClick) {
        Text(if (selected) "✓ $text" else text)
    }
}

@Composable
fun BattleScreen(player: Fighter, enemy: Fighter, scenario: Scenario, outfit: Outfit, onExit: () -> Unit) {
    var playerHp by remember { mutableIntStateOf(100) }
    var enemyHp by remember { mutableIntStateOf(100) }
    var playerPower by remember { mutableFloatStateOf(0f) }
    var enemyPower by remember { mutableFloatStateOf(0f) }
    var charging by remember { mutableStateOf(false) }
    var status by remember { mutableStateOf("Luta começou!") }

    LaunchedEffect(charging) {
        while (charging && playerPower < 100f) {
            delay(80)
            playerPower = (playerPower + 1.5f).coerceAtMost(100f)
        }
    }

    LaunchedEffect(enemyHp, playerHp) {
        if (enemyHp > 0 && playerHp > 0) {
            delay(1300)
            if (Random.nextBoolean()) {
                playerHp = max(0, playerHp - enemy.punch)
                enemyPower = (enemyPower + 8).coerceAtMost(100f)
                status = "${enemy.name} acertou soco!"
            } else {
                playerHp = max(0, playerHp - enemy.kick)
                enemyPower = (enemyPower + 10).coerceAtMost(100f)
                status = "${enemy.name} acertou chute!"
            }
            if (enemyPower >= 100f && playerHp > 0) {
                playerHp = max(0, playerHp - enemy.specialDamage)
                enemyPower = 0f
                status = "${enemy.name} usou ${enemy.powerName}!"
            }
        }
    }

    val playerHpAnim by animateFloatAsState(playerHp / 100f, label = "php")
    val enemyHpAnim by animateFloatAsState(enemyHp / 100f, label = "ehp")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(scenarioBrush(scenario))
            .padding(12.dp)
    ) {
        Text("${scenario.label} • Roupa: ${outfit.label}", color = Color.White)
        HealthBar("${player.name} (${playerHp} HP)", playerHpAnim, player.color)
        HealthBar("${enemy.name} (${enemyHp} HP)", enemyHpAnim, enemy.color)

        Row(
            Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CharacterArt(player, outfit)
            CharacterArt(enemy, outfit)
        }

        Text(status, color = Color.White)
        Text("Energia do poder", color = Color.White)
        LinearProgressIndicator(progress = { playerPower / 100f }, modifier = Modifier.fillMaxWidth())

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                enemyHp = max(0, enemyHp - player.punch)
                playerPower = (playerPower + 7).coerceAtMost(100f)
                status = "${player.name} acertou soco"
            }, enabled = playerHp > 0 && enemyHp > 0) { Text("Soco") }
            Button(onClick = {
                enemyHp = max(0, enemyHp - player.kick)
                playerPower = (playerPower + 9).coerceAtMost(100f)
                status = "${player.name} acertou chute"
            }, enabled = playerHp > 0 && enemyHp > 0) { Text("Chute") }
            Button(
                onClick = {
                    if (playerPower >= 100f) {
                        enemyHp = max(0, enemyHp - player.specialDamage)
                        status = "${player.name} usou ${player.powerName}!"
                        playerPower = 0f
                    }
                },
                enabled = playerPower >= 100f && playerHp > 0 && enemyHp > 0
            ) { Text("Poder") }
        }

        Button(
            onClick = {},
            modifier = Modifier.pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        charging = true
                        tryAwaitRelease()
                        charging = false
                    }
                )
            }
        ) {
            Text("Segure para carregar energia")
        }

        if (enemyHp == 0 || playerHp == 0) {
            Text(
                text = if (enemyHp == 0) "Vitória!" else "Derrota!",
                color = Color.Yellow,
                style = MaterialTheme.typography.headlineSmall
            )
            Button(onClick = onExit) { Text("Voltar ao menu") }
        }
    }
}

@Composable
fun HealthBar(label: String, progress: Float, color: Color) {
    Text(label, color = Color.White)
    LinearProgressIndicator(
        progress = { progress.coerceIn(0f, 1f) },
        modifier = Modifier.fillMaxWidth(),
        color = color,
        trackColor = Color.DarkGray
    )
}

@Composable
fun CharacterArt(fighter: Fighter, outfit: Outfit) {
    val outfitColor = when (outfit) {
        Outfit.BIKINI -> Color(0xFFFFF176)
        Outfit.LINGERIE -> Color(0xFFF48FB1)
    }

    Canvas(modifier = Modifier.size(160.dp)) {
        drawCircle(color = Color(0xFFFFE0BD), radius = 28f, center = Offset(size.width / 2, 32f))
        drawCircle(color = fighter.color, radius = 16f, center = Offset(size.width / 2, 26f), style = Stroke(8f))
        drawRoundRect(
            color = outfitColor,
            topLeft = Offset(size.width / 2 - 24f, 60f),
            size = androidx.compose.ui.geometry.Size(48f, 62f)
        )
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(size.width / 2 - 22f, 70f),
            end = Offset(size.width / 2 - 54f, 105f),
            strokeWidth = 14f
        )
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(size.width / 2 + 22f, 70f),
            end = Offset(size.width / 2 + 54f, 105f),
            strokeWidth = 14f
        )
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(size.width / 2 - 14f, 122f),
            end = Offset(size.width / 2 - 20f, 154f),
            strokeWidth = 15f
        )
        drawLine(
            color = Color(0xFFFFE0BD),
            start = Offset(size.width / 2 + 14f, 122f),
            end = Offset(size.width / 2 + 20f, 154f),
            strokeWidth = 15f
        )
    }
}

fun scenarioBrush(scenario: Scenario): Brush = when (scenario) {
    Scenario.CIDADE -> Brush.verticalGradient(listOf(Color(0xFF212121), Color(0xFF455A64)))
    Scenario.PRAIA -> Brush.verticalGradient(listOf(Color(0xFF26C6DA), Color(0xFFFFF59D)))
    Scenario.CASA -> Brush.verticalGradient(listOf(Color(0xFF8D6E63), Color(0xFFD7CCC8)))
}
