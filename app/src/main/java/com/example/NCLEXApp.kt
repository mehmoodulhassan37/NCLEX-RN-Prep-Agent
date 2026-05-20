package com.example

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class Difficulty { EASY, MEDIUM, HARD }
enum class AppState { WELCOME, TEST, RATIONALE, DASHBOARD }

data class NCLEXQuestion(
    val id: Int,
    val stem: String,
    val options: List<String>,
    val correctOptionIndex: Int,
    val rationale: String,
    val category: String,
    val difficulty: Difficulty
)

object QuestionBank {
    val questions = listOf(
        // EASY
        NCLEXQuestion(1, "A client with heart failure is prescribed furosemide. Which of the following should the nurse monitor most closely?", listOf("Serum potassium levels", "Blood glucose", "Serum calcium", "Hemoglobin A1c"), 0, "Furosemide is a loop diuretic that depletes potassium, leading to hypokalemia which can cause dysrhythmias.", "Pharmacology", Difficulty.EASY),
        NCLEXQuestion(2, "When administering a subcutaneous injection of heparin, what is the correct action for the nurse to take?", listOf("Massage the injection site", "Aspirate for blood prior to injecting", "Inject clearly into the abdominal adipose tissue", "Use a 22-gauge, 1.5-inch needle"), 2, "Heparin should be given in the abdomen subcutaneously. Massaging or aspirating is contraindicated as it may cause bruising or bleeding.", "Safe & Effective Care", Difficulty.EASY),
        NCLEXQuestion(3, "Which finding in a newborn is considered a normal physiological response?", listOf("Nasal flaring", "Acrocyanosis", "Grunting on expiration", "Central cyanosis"), 1, "Acrocyanosis (bluish hands and feet) is normal in the first 24-48 hours of life due to immature peripheral circulation.", "Health Promotion", Difficulty.EASY),
        NCLEXQuestion(4, "A client with diabetes mellitus is experiencing shakiness, sweating, and confusion. What is the priority nursing intervention?", listOf("Administer regular insulin", "Provide a fast-acting carbohydrate", "Assess the blood pressure", "Encourage the client to rest"), 1, "These are signs of hypoglycemia. Providing a fast-acting carbohydrate (like juice) is the immediate priority.", "Physiological Integrity", Difficulty.EASY),
        NCLEXQuestion(5, "In a patient with chronic kidney disease, which dietary restriction is most typical?", listOf("Low calcium diet", "Low protein and low potassium diet", "High sodium diet", "Fluid overload diet"), 1, "Patients with CKD need low protein to reduce uremic toxins and low potassium to prevent hyperkalemia.", "Physiological Adaptation", Difficulty.EASY),

        // MEDIUM
        NCLEXQuestion(6, "A client is receiving a continuous IV infusion of magnesium sulfate for preeclampsia. Which finding indicates magnesium toxicity?", listOf("Hyperactive deep tendon reflexes", "Respiratory rate of 10 breaths/min", "Urine output of 60 mL/hr", "Blood pressure 140/90"), 1, "Magnesium toxicity causes CNS depression, leading to hyporeflexia, oliguria, and respiratory depression (<12 breaths/min).", "Pharmacology", Difficulty.MEDIUM),
        NCLEXQuestion(7, "A post-operative client exhibits a sudden onset of chest pain and dyspnea. The nurse suspects a pulmonary embolism. What is the initial action?", listOf("Administer an electrocardiogram", "Elevate the head of the bed and apply oxygen", "Call the rapid response team", "Draw blood for D-dimer"), 1, "Elevating the head of the bed and administering oxygen addresses the immediate airway and breathing needs before gathering further data.", "Physiological Integrity", Difficulty.MEDIUM),
        NCLEXQuestion(8, "A patient with schizophrenia is pacing the hall and claiming that the FBI is listening to their thoughts. What is the most appropriate nursing response?", listOf("Tell the patient the FBI is not real.", "Ask what the FBI is saying.", "Acknowledge the fear but state that you do not hear anyone.", "Medicate the patient immediately."), 2, "It's important to acknowledge the patient's feelings (fear) while gently presenting reality without arguing.", "Psychosocial Integrity", Difficulty.MEDIUM),
        NCLEXQuestion(9, "The nurse is assessing a chest tube drainage system and notes continuous bubbling in the water seal chamber. What does this indicate?", listOf("The lungs have fully re-expanded.", "Normal functioning of the system.", "An air leak in the system.", "Need to increase suction."), 2, "Continuous bubbling in the water seal chamber indicates an air leak, whereas intermittent bubbling is normal with exhalation.", "Safe & Effective Care", Difficulty.MEDIUM),
        NCLEXQuestion(10, "Which patient is at the highest risk for developing skin breakdown?", listOf("A 30-year-old with a femur fracture in a cast.", "An 80-year-old bedbound patient with urinary incontinence.", "A 50-year-old diabetic patient who walks daily.", "A 20-year-old with pneumonia on bedrest for 2 days."), 1, "Advanced age, immobility, and moisture (incontinence) are major risk factors for skin breakdown and pressure injuries.", "Basic Care & Comfort", Difficulty.MEDIUM),
        NCLEXQuestion(11, "A patient taking digoxin reports seeing yellow halos around lights. What lab value should the nurse check first?", listOf("Serum creatinine", "Serum potassium", "Serum sodium", "Serum magnesium"), 1, "Hypokalemia increases the risk for digoxin toxicity, which presents with visual disturbances such as yellow halos.", "Pharmacology", Difficulty.MEDIUM),

        // HARD
        NCLEXQuestion(12, "A patient is admitted with a diagnosis of acute pancreatitis. Which laboratory finding is most specific to this condition?", listOf("Elevated serum amylase", "Elevated serum lipase", "Decreased serum calcium", "Elevated white blood cell count"), 1, "While both amylase and lipase elevate, serum lipase is more specific to the pancreas and stays elevated longer.", "Physiological Adaptation", Difficulty.HARD),
        NCLEXQuestion(13, "A client with a traumatic brain injury is exhibiting Cushing's triad. Which set of vital signs aligns with this finding?", listOf("BP 90/60, HR 120, RR 24", "BP 160/70, HR 45, RR 10 (irregular)", "BP 140/90, HR 90, RR 18", "BP 80/40, HR 140, RR 30"), 1, "Cushing's triad consists of widening pulse pressure (systolic hypertension), bradycardia, and irregular respirations, indicating increased ICP.", "Physiological Integrity", Difficulty.HARD),
        NCLEXQuestion(14, "The nurse is caring for a patient on mechanical ventilation. The high-pressure alarm sounds. Which of the following is a potential cause?", listOf("Disconnection of the ventilator tubing.", "Patient biting the endotracheal tube.", "Cuff leak in the endotracheal tube.", "Extubation."), 1, "High pressure indicates resistance. Biting the tube, coughing, secretions, or a kink can all trigger a high-pressure alarm.", "Safe & Effective Care", Difficulty.HARD),
        NCLEXQuestion(15, "A client with Addison's disease is admitted with a crisis. What physiological disturbance is most critical to correct?", listOf("Hyperglycemia", "Hypokalemia", "Hyponatremia and Hypotension", "Hypercalcemia"), 2, "Addisonian crisis involves acute adrenal insufficiency, leading to profound sodium and fluid loss, hyperkalemia, and shock.", "Physiological Adaptation", Difficulty.HARD),
        NCLEXQuestion(16, "A pediatric patient with Tetralogy of Fallot is having a 'tet spell'. What is the optimal positioning for this child?", listOf("Prone position", "Trendelenburg position", "Knee-to-chest position", "Supine position"), 2, "Knee-to-chest position increases systemic vascular resistance, decreasing the right-to-left shunt and improving oxygenation.", "Health Promotion", Difficulty.HARD)
    )
    
    fun getQuestionsForDifficulty(difficulty: Difficulty): List<NCLEXQuestion> {
        return questions.filter { it.difficulty == difficulty }.shuffled()
    }
}

data class AppStateData(
    val appState: AppState = AppState.WELCOME,
    val currentDifficulty: Difficulty = Difficulty.MEDIUM,
    val questionsAsked: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 15,
    val currentQuestion: NCLEXQuestion? = null,
    val selectedOptionIndex: Int? = null,
    val usedQuestionIds: Set<Int> = emptySet(),
    val isCorrect: Boolean = false
)

class NCLEXViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppStateData())
    val uiState: StateFlow<AppStateData> = _uiState.asStateFlow()

    fun startExam() {
        _uiState.value = AppStateData() // Reset
        loadNextQuestion(Difficulty.MEDIUM)
    }

    private fun loadNextQuestion(difficulty: Difficulty) {
        val available = QuestionBank.getQuestionsForDifficulty(difficulty).filter { it.id !in _uiState.value.usedQuestionIds }
        val nextQuestion = if (available.isNotEmpty()) {
            available.first()
        } else {
            // Fallback to any difficulty if exhausted, though unlikely with 15 questions and a proper bank size
            QuestionBank.questions.firstOrNull { it.id !in _uiState.value.usedQuestionIds }
        }

        if (nextQuestion == null || _uiState.value.questionsAsked >= _uiState.value.totalQuestions) {
            _uiState.update { it.copy(appState = AppState.DASHBOARD) }
        } else {
            _uiState.update {
                it.copy(
                    appState = AppState.TEST,
                    currentQuestion = nextQuestion,
                    currentDifficulty = difficulty,
                    selectedOptionIndex = null,
                    usedQuestionIds = it.usedQuestionIds + nextQuestion.id
                )
            }
        }
    }

    fun submitAnswer(optionIndex: Int) {
        val q = _uiState.value.currentQuestion ?: return
        val correct = optionIndex == q.correctOptionIndex
        
        _uiState.update {
            it.copy(
                appState = AppState.RATIONALE,
                selectedOptionIndex = optionIndex,
                isCorrect = correct,
                correctAnswers = if (correct) it.correctAnswers + 1 else it.correctAnswers,
                questionsAsked = it.questionsAsked + 1
            )
        }
    }
    
    fun nextQuestion() {
        val wasCorrect = _uiState.value.isCorrect
        
        val nextDifficulty = if (wasCorrect) {
            Difficulty.HARD
        } else {
            Difficulty.EASY
        }
        
        loadNextQuestion(nextDifficulty)
    }
    
    fun restart() {
        startExam()
    }
}

@Composable
fun NCLEXApp() {
    val viewModel: NCLEXViewModel = viewModel()
    val state by viewModel.uiState.collectAsState()

    Crossfade(targetState = state.appState, animationSpec = tween(500), label = "ScreenTransition") { screen ->
        when (screen) {
            AppState.WELCOME -> WelcomeScreen(onStart = { viewModel.startExam() })
            AppState.TEST -> TestSimulationScreen(state = state, onSubmit = { viewModel.submitAnswer(it) })
            AppState.RATIONALE -> RationaleScreen(state = state, onNext = { viewModel.nextQuestion() })
            AppState.DASHBOARD -> PerformanceDashboard(state = state, onRestart = { viewModel.restart() })
        }
    }
}

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "App Icon",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "NCLEX-RN Prep Agent",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Welcome to your adaptive NCLEX-RN simulator. The difficulty will dynamically adjust based on your performance, mimicking the real exam experience.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("start_exam_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Start Adaptive Exam", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun TestSimulationScreen(state: AppStateData, onSubmit: (Int) -> Unit) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    val currentQ = state.currentQuestion ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.questionsAsked + 1} of ${state.totalQuestions}",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Surface(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "${currentQ.category} • ${currentQ.difficulty.name}",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = currentQ.stem,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            itemsIndexed(currentQ.options) { index, option ->
                val isSelected = selectedOption == index
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedOption = index }
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedOption = index }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Button(
            onClick = { selectedOption?.let { onSubmit(it) } },
            enabled = selectedOption != null,
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("submit_answer_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Submit Answer", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun RationaleScreen(state: AppStateData, onNext: () -> Unit) {
    val q = state.currentQuestion ?: return
    val selected = state.selectedOptionIndex ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing)
    ) {
        Row(
             modifier = Modifier.fillMaxWidth(),
             horizontalArrangement = Arrangement.SpaceBetween,
             verticalAlignment = Alignment.CenterVertically
        ) {
             Text(
                 text = "Result",
                 style = MaterialTheme.typography.labelLarge,
                 color = MaterialTheme.colorScheme.primary
             )
        }
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = q.stem,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(24.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.weight(1f)) {
            itemsIndexed(q.options) { index, option ->
                val isCorrectAnswer = index == q.correctOptionIndex
                val isSelectedWrong = index == selected && !isCorrectAnswer
                
                val bgColor = when {
                    isCorrectAnswer -> com.example.ui.theme.SuccessGreen.copy(alpha = 0.15f)
                    isSelectedWrong -> com.example.ui.theme.ErrorRed.copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }
                val borderColor = when {
                	isCorrectAnswer -> com.example.ui.theme.SuccessGreen
                	isSelectedWrong -> com.example.ui.theme.ErrorRed
                	else -> MaterialTheme.colorScheme.outlineVariant
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (isCorrectAnswer) {
                            Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = com.example.ui.theme.SuccessGreen)
                        } else if (isSelectedWrong) {
                            Icon(Icons.Filled.Clear, contentDescription = null, tint = com.example.ui.theme.ErrorRed)
                        } else {
                            Spacer(modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            
            item {
                Spacer(modifier = Modifier.height(24.dp))
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Detailed Clinical Rationale",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = q.rationale,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth().height(56.dp).testTag("next_question_button"),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Next Question", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
        }
    }
}

@Composable
fun PerformanceDashboard(state: AppStateData, onRestart: () -> Unit) {
    val accuracy = if (state.totalQuestions > 0) ((state.correctAnswers.toFloat() / state.totalQuestions.toFloat()) * 100).toInt() else 0
    val performanceBand = when {
    	accuracy >= 80 -> "Passing Likelihood: High"
    	accuracy in 60..79 -> "Passing Likelihood: Moderate"
    	else -> "Passing Likelihood: Low"
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Exam Completed",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(24.dp))
                
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(60.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .border(4.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(60.dp))
                ) {
                    Text(
                        text = "$accuracy%",
                        style = MaterialTheme.typography.displayMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(
                	modifier = Modifier.fillMaxWidth(),
                	horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                	Column(horizontalAlignment = Alignment.CenterHorizontally) {
                		Text("Correct", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                		Text("${state.correctAnswers}", style = MaterialTheme.typography.headlineSmall, color = com.example.ui.theme.SuccessGreen, fontWeight = FontWeight.Bold)
                	}
                	Column(horizontalAlignment = Alignment.CenterHorizontally) {
                		Text("Incorrect", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                		Text("${state.questionsAsked - state.correctAnswers}", style = MaterialTheme.typography.headlineSmall, color = com.example.ui.theme.ErrorRed, fontWeight = FontWeight.Bold)
                	}
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Surface(
                	color = MaterialTheme.colorScheme.secondaryContainer,
                	shape = RoundedCornerShape(12.dp)
                ) {
                	Text(
                		text = performanceBand,
                		modifier = Modifier.padding(16.dp),
                		style = MaterialTheme.typography.titleMedium,
                		color = MaterialTheme.colorScheme.onSecondaryContainer,
                		fontWeight = FontWeight.Bold
                	)
                }
                
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onRestart,
                    modifier = Modifier.fillMaxWidth().height(56.dp).testTag("restart_exam_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Retake Exam", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
