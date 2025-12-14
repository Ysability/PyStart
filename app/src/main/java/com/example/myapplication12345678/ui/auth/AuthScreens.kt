package com.example.myapplication12345678.ui.auth

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication12345678.data.Course
import com.example.myapplication12345678.data.ExcelExporter
import com.example.myapplication12345678.data.Lesson
import com.example.myapplication12345678.data.LocalDatabase
import com.example.myapplication12345678.data.UserStats
import com.example.myapplication12345678.data.SupportMessage
import com.example.myapplication12345678.ui.theme.AccentOrange
import com.example.myapplication12345678.ui.theme.AccentPink
import com.example.myapplication12345678.ui.theme.DarkBackground
import com.example.myapplication12345678.ui.theme.GradientEnd
import com.example.myapplication12345678.ui.theme.GradientStart
import com.example.myapplication12345678.ui.theme.LightBackground
import com.example.myapplication12345678.ui.theme.SecondaryBlue
import com.example.myapplication12345678.ui.theme.SuccessGreen
import com.example.myapplication12345678.ui.theme.ThemeState

@Composable
fun AuthRoot() {
    var selectedTab by remember { mutableStateOf(0) }
    var currentScreen by remember { mutableStateOf("auth") }
    var currentUserLogin by remember { mutableStateOf<String?>(null) }
    var isGuestMode by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val db = remember { LocalDatabase(context) }

    val isDark = ThemeState.isDarkTheme
    val modernGradient = Brush.verticalGradient(
        colors = if (isDark) listOf(
            Color(0xFF0F0F23),
            Color(0xFF1A1A2E),
            Color(0xFF16213E)
        ) else listOf(
            Color(0xFFF5F5F7),
            Color(0xFFE8E8ED),
            Color(0xFFDDDDE5)
        )
    )
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.1f)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(modernGradient)
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Переключатель темы в углу
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .clickable { ThemeState.isDarkTheme = !ThemeState.isDarkTheme }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (isDark) "☀️ Светлая" else "🌙 Тёмная",
                            color = textColor,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Логотип только на экранах auth и user
                if (currentScreen != "admin") {
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                brush = Brush.linearGradient(listOf(GradientStart, GradientEnd)),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🐍",
                            fontSize = 40.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "PyStart",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Text(
                        text = when (currentScreen) {
                            "auth" -> if (selectedTab == 0) "Войдите в аккаунт" else "Создайте новый аккаунт"
                            "user" -> if (isGuestMode) "Гостевой режим" else "Изучайте Python легко"
                            else -> ""
                        },
                        fontSize = 14.sp,
                        color = textColor.copy(alpha = 0.6f)
                    )

                    Spacer(modifier = Modifier.height(32.dp))
                }

                when (currentScreen) {
                    "auth" -> {
                        AuthTabs(selectedTab = selectedTab, onTabSelected = { index ->
                            Log.d("Auth", "Tab changed to index=$index")
                            errorMessage = null
                            selectedTab = index
                        }, isDark = isDark)

                        Spacer(modifier = Modifier.height(16.dp))

                        // Сообщение об ошибке
                        errorMessage?.let { msg ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(AccentPink.copy(alpha = 0.2f))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = msg,
                                    color = AccentPink,
                                    fontSize = 13.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                        }

                        // Стеклянная карточка
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(28.dp))
                                .background(cardBg)
                                .border(
                                    width = 1.dp,
                                    color = borderColor,
                                    shape = RoundedCornerShape(28.dp)
                                )
                                .padding(24.dp)
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (selectedTab == 0) {
                                    LoginScreen(
                                        isDark = isDark,
                                        onLoginSuccess = { isAdmin, login ->
                                            errorMessage = null
                                            isGuestMode = false
                                            if (!isAdmin) {
                                                currentUserLogin = login
                                            }
                                            currentScreen = if (isAdmin) "admin" else "user"
                                        },
                                        onError = { errorMessage = it },
                                        onGuestLogin = {
                                            errorMessage = null
                                            isGuestMode = true
                                            currentUserLogin = null
                                            currentScreen = "user"
                                        }
                                    )
                                } else {
                                    RegistrationScreen(
                                        isDark = isDark,
                                        onRegistered = { login ->
                                            errorMessage = null
                                            isGuestMode = false
                                            currentUserLogin = login
                                            currentScreen = "user"
                                        },
                                        onError = { errorMessage = it }
                                    )
                                }
                            }
                        }

                    }

                    "user" -> {
                        UserMainScreen(
                            db = db,
                            login = currentUserLogin,
                            isGuest = isGuestMode,
                            isDark = isDark,
                            onLogout = {
                                isGuestMode = false
                                currentScreen = "auth"
                                selectedTab = 0
                            }
                        )
                    }

                    "admin" -> {
                        AdminScreen(
                            isDark = isDark,
                            onLogout = {
                                currentScreen = "auth"
                                selectedTab = 0
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AuthTabs(selectedTab: Int, onTabSelected: (Int) -> Unit, isDark: Boolean) {
    val titles = listOf("Вход", "Регистрация")
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        titles.forEachIndexed { index, title ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (isSelected) Color.White else textColor.copy(alpha = 0.6f),
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun LoginScreen(
    isDark: Boolean,
    onLoginSuccess: (isAdmin: Boolean, login: String?) -> Unit,
    onError: (String) -> Unit,
    onGuestLogin: () -> Unit = {}
) {
    val context = LocalContext.current
    val db = remember { LocalDatabase(context) }
    var login by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Column {
        ModernTextField(
            value = login,
            onValueChange = { login = it },
            label = "Логин",
            icon = "👤",
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(
            value = password,
            onValueChange = { password = it },
            label = "Пароль",
            icon = "🔒",
            isPassword = true,
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = "Войти",
            onClick = {
                Log.d("Auth", "Login clicked: login=$login")

                if (login.isBlank() || password.isBlank()) {
                    onError("Заполните все поля")
                    return@GradientButton
                }

                if (login == "admin" && password == "admin123") {
                    onLoginSuccess(true, login)
                    return@GradientButton
                }

                try {
                    val cursor = db.readableDatabase.rawQuery(
                        "SELECT first_name, last_name, login, email FROM users WHERE login = ? AND password = ?",
                        arrayOf(login, password)
                    )
                    val success = cursor.use { it.moveToFirst() }

                    if (success) {
                        Log.d("Auth", "User login success: login=$login")
                        onLoginSuccess(false, login)
                    } else {
                        Log.d("Auth", "User login failed for login=$login")
                        onError("Неверный логин или пароль")
                    }
                } catch (e: Exception) {
                    Log.e("Auth", "Login error: ${e.message}")
                    onError("Ошибка входа: ${e.message}")
                }
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка гостевого входа
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))
                .clickable { onGuestLogin() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "👤 Войти как гость",
                color = textColor.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка забыли пароль
        var showForgotPassword by remember { mutableStateOf(false) }
        var forgotLogin by remember { mutableStateOf("") }
        var forgotSecretWord by remember { mutableStateOf("") }
        var newPassword by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        var verificationSuccess by remember { mutableStateOf(false) }
        var forgotError by remember { mutableStateOf("") }

        if (!showForgotPassword) {
            Text(
                text = "Забыли пароль?",
                color = SecondaryBlue,
                fontSize = 14.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showForgotPassword = true },
                textAlign = TextAlign.Center
            )
        } else {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (verificationSuccess) "🔐 Новый пароль" else "🔑 Восстановление",
                            color = textColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "✕",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 18.sp,
                            modifier = Modifier.clickable {
                                showForgotPassword = false
                                verificationSuccess = false
                                forgotLogin = ""
                                forgotSecretWord = ""
                                newPassword = ""
                                confirmPassword = ""
                                forgotError = ""
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (!verificationSuccess) {
                        ModernTextField(
                            value = forgotLogin,
                            onValueChange = { forgotLogin = it },
                            label = "Логин",
                            icon = "👤",
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ModernTextField(
                            value = forgotSecretWord,
                            onValueChange = { forgotSecretWord = it },
                            label = "Секретное слово",
                            icon = "🔑",
                            isDark = isDark
                        )

                        if (forgotError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = forgotError,
                                color = AccentPink,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GradientButton(
                            text = "Проверить",
                            onClick = {
                                if (forgotLogin.isBlank() || forgotSecretWord.isBlank()) {
                                    forgotError = "Заполните все поля"
                                    return@GradientButton
                                }
                                if (db.verifySecretWord(forgotLogin, forgotSecretWord)) {
                                    verificationSuccess = true
                                    forgotError = ""
                                } else {
                                    forgotError = "Неверный логин или секретное слово"
                                }
                            }
                        )
                    } else {
                        ModernTextField(
                            value = newPassword,
                            onValueChange = { newPassword = it },
                            label = "Новый пароль",
                            icon = "🔒",
                            isPassword = true,
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        ModernTextField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Подтвердите пароль",
                            icon = "🔒",
                            isPassword = true,
                            isDark = isDark
                        )

                        if (forgotError.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = forgotError,
                                color = AccentPink,
                                fontSize = 12.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        GradientButton(
                            text = "Сохранить пароль",
                            onClick = {
                                if (newPassword.isBlank() || confirmPassword.isBlank()) {
                                    forgotError = "Заполните все поля"
                                    return@GradientButton
                                }
                                if (newPassword != confirmPassword) {
                                    forgotError = "Пароли не совпадают"
                                    return@GradientButton
                                }
                                if (db.updatePassword(forgotLogin, newPassword)) {
                                    showForgotPassword = false
                                    verificationSuccess = false
                                    forgotLogin = ""
                                    forgotSecretWord = ""
                                    newPassword = ""
                                    confirmPassword = ""
                                    forgotError = ""
                                } else {
                                    forgotError = "Ошибка сохранения пароля"
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ModernTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: String,
    isPassword: Boolean = false,
    isDark: Boolean = true
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.2f)

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label, color = textColor.copy(alpha = 0.6f)) },
        leadingIcon = { Text(icon, fontSize = 18.sp) },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GradientStart,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = GradientStart
        ),
        shape = RoundedCornerShape(16.dp),
        singleLine = true
    )
}

@Composable
private fun GradientButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

@Composable
fun RegistrationScreen(
    isDark: Boolean,
    onRegistered: (login: String) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val db = remember { LocalDatabase(context) }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var login by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var secretWord by remember { mutableStateOf("") }
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var avatarPath by remember { mutableStateOf<String?>(null) }

    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    // Launcher для выбора изображения из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                avatarBitmap = bitmap
                
                // Временно сохраняем путь (будет обновлён после регистрации)
                val fileName = "avatar_temp_${System.currentTimeMillis()}.jpg"
                val file = java.io.File(context.filesDir, fileName)
                val outputStream = java.io.FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.close()
                avatarPath = file.absolutePath
            } catch (e: Exception) {
                Log.e("Registration", "Error loading avatar: ${e.message}")
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Аватарка с возможностью загрузки из галереи
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap!!.asImageBitmap(),
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "📷", fontSize = 32.sp)
                    Text(
                        text = "Добавить фото",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (avatarBitmap != null) "Нажмите для изменения" else "Нажмите для загрузки фото",
            color = textColor.copy(alpha = 0.5f),
            fontSize = 12.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernTextField(value = firstName, onValueChange = { firstName = it }, label = "Имя", icon = "👤", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия", icon = "👥", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = login, onValueChange = { login = it }, label = "Логин", icon = "🆔", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = email, onValueChange = { email = it }, label = "Email", icon = "📧", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = password, onValueChange = { password = it }, label = "Пароль", icon = "🔒", isPassword = true, isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = secretWord, onValueChange = { secretWord = it }, label = "Секретное слово для восстановления", icon = "🔑", isDark = isDark)
        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = "Зарегистрироваться",
            onClick = {
                if (firstName.isBlank() || lastName.isBlank() || login.isBlank() || email.isBlank() || password.isBlank() || secretWord.isBlank()) {
                    onError("Заполните все поля")
                    return@GradientButton
                }

                try {
                    // Проверка, есть ли уже такой логин
                    val checkCursor = db.readableDatabase.rawQuery(
                        "SELECT login FROM users WHERE login = ?",
                        arrayOf(login)
                    )
                    val exists = checkCursor.use { it.moveToFirst() }

                    if (exists) {
                        onError("Пользователь с таким логином уже существует")
                        return@GradientButton
                    }

                    // Если есть аватар, переименовываем файл с правильным именем
                    val finalAvatarPath = if (avatarPath != null) {
                        val oldFile = java.io.File(avatarPath!!)
                        val newFileName = "avatar_${login}_${System.currentTimeMillis()}.jpg"
                        val newFile = java.io.File(context.filesDir, newFileName)
                        oldFile.renameTo(newFile)
                        newFile.absolutePath
                    } else {
                        "" // Пустая строка если аватар не загружен
                    }

                    db.writableDatabase.execSQL(
                        "INSERT INTO users (first_name, last_name, login, email, password, avatar, secret_word) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        arrayOf(firstName, lastName, login, email, password, finalAvatarPath, secretWord)
                    )
                    Log.d("Auth", "User registered: $login")
                    onRegistered(login)
                } catch (e: Exception) {
                    Log.e("Auth", "Registration error: ${e.message}")
                    onError("Ошибка регистрации: ${e.message}")
                }
            }
        )
    }
}

@Composable
fun UserMainScreen(
    db: LocalDatabase,
    login: String?,
    isGuest: Boolean,
    isDark: Boolean,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    var selectedCourse by remember { mutableStateOf<Course?>(null) }
    var selectedLesson by remember { mutableStateOf<Lesson?>(null) }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var courses by remember { mutableStateOf(db.getAllCourses()) }
    var favoriteCourses by remember { mutableStateOf(if (login != null) db.getFavoriteCourses(login) else emptyList()) }

    var showGuestCourseMessage by remember { mutableStateOf(false) }
    var showSupportChat by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Показываем чат поддержки если открыт
        if (showSupportChat && login != null) {
            SupportChatScreen(
                db = db,
                userLogin = login,
                isDark = isDark,
                onBack = { showSupportChat = false }
            )
        } else if (selectedLesson != null) {
            // Показываем урок если выбран
            LessonDetailScreen(
                lesson = selectedLesson!!,
                isDark = isDark,
                onBack = { selectedLesson = null },
                onComplete = {
                    if (login != null) {
                        db.markLessonCompleted(login, selectedLesson!!.id)
                    }
                }
            )
        } else if (showGuestCourseMessage && isGuest) {
            // Сообщение для гостя при попытке открыть курс
            GuestCourseContent(
                isDark = isDark,
                onBack = { showGuestCourseMessage = false }
            )
        } else if (selectedCourse != null) {
            // Показываем уроки курса
            CourseLessonsScreen(
                db = db,
                course = selectedCourse!!,
                login = login,
                isDark = isDark,
                onBack = { selectedCourse = null },
                onLessonClick = { selectedLesson = it },
                onFavoriteChanged = { if (login != null) favoriteCourses = db.getFavoriteCourses(login) }
            )
        } else {
            // Современные табы с избранным
            MainMenuTabsWithFavorites(selectedTab = selectedTab, onTabSelected = { selectedTab = it }, isDark = isDark, isGuest = isGuest)

            Spacer(modifier = Modifier.height(20.dp))

            // Стеклянная карточка с контентом
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(cardBg)
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    when (selectedTab) {
                        0 -> CoursesTabContentNew(
                            courses = courses,
                            isDark = isDark,
                            onCourseClick = { course ->
                                if (isGuest) {
                                    showGuestCourseMessage = true
                                } else {
                                    selectedCourse = course
                                }
                            }
                        )
                        1 -> if (isGuest) {
                            GuestProfileContent(isDark = isDark)
                        } else {
                            FavoritesTabContent(
                                courses = favoriteCourses,
                                isDark = isDark,
                                onCourseClick = { selectedCourse = it }
                            )
                        }
                        2 -> if (isGuest) {
                            GuestProfileContent(isDark = isDark)
                        } else {
                            ProfileTabContent(
                                db = db,
                                login = login,
                                isDark = isDark,
                                onLogout = onLogout,
                                onOpenSupport = { showSupportChat = true }
                            )
                        }
                        3 -> if (isGuest) {
                            GuestStatsContent(isDark = isDark)
                        } else {
                            StatsTabContent(db = db, login = login, isDark = isDark)
                        }
                    }
                }
            }

            // Кнопка выхода только для гостя (у пользователя она в профиле)
            if (isGuest) {
                Spacer(modifier = Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(
                            width = 1.dp,
                            color = AccentPink.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Выйти из гостевого режима",
                        color = AccentPink,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun MainMenuTabs(selectedTab: Int, onTabSelected: (Int) -> Unit, isDark: Boolean) {
    val tabs = listOf(
        Pair("📚", "Курсы"),
        Pair("👤", "Профиль"),
        Pair("📊", "Статистика")
    )
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        tabs.forEachIndexed { index, (icon, title) ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = icon, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else textColor.copy(alpha = 0.5f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun MainMenuTabsWithFavorites(selectedTab: Int, onTabSelected: (Int) -> Unit, isDark: Boolean, isGuest: Boolean) {
    val tabs = if (isGuest) {
        listOf(
            Pair("📚", "Курсы"),
            Pair("👤", "Профиль"),
            Pair("📊", "Статистика")
        )
    } else {
        listOf(
            Pair("📚", "Курсы"),
            Pair("⭐", "Избранное"),
            Pair("👤", "Профиль"),
            Pair("📊", "Статистика")
        )
    }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        tabs.forEachIndexed { index, (icon, title) ->
            val isSelected = selectedTab == index
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                        else Brush.linearGradient(listOf(Color.Transparent, Color.Transparent))
                    )
                    .clickable { onTabSelected(index) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = icon, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = title,
                        color = if (isSelected) Color.White else textColor.copy(alpha = 0.5f),
                        fontSize = 9.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun CoursesTabContentWithFavorites(
    db: LocalDatabase,
    courses: List<Course>,
    login: String?,
    isDark: Boolean,
    onCourseClick: (Course) -> Unit,
    onFavoriteChanged: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    var levelFilter by remember { mutableStateOf("Все") }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.06f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f)

    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("🔍 Поиск курсов", color = textColor.copy(alpha = 0.6f)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GradientStart,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = GradientStart
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Фильтры
    val levels = listOf("Все", "Начальный", "Средний", "Продвинутый")
    Column {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(if (levelFilter == "Все") Brush.linearGradient(listOf(GradientStart, GradientEnd)) else Brush.linearGradient(listOf(bgColor, bgColor)))
                .clickable { levelFilter = "Все" }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Все уровни",
                color = if (levelFilter == "Все") Color.White else textColor.copy(alpha = 0.7f),
                fontWeight = if (levelFilter == "Все") FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            levels.drop(1).forEach { level ->
                val isSelected = levelFilter == level
                val levelColor = when (level) {
                    "Начальный" -> SuccessGreen
                    "Средний" -> AccentOrange
                    else -> AccentPink
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) levelColor.copy(alpha = 0.3f) else bgColor)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) levelColor else borderColor,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { levelFilter = level }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = level,
                        color = if (isSelected) levelColor else textColor.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val filteredCourses = courses.filter { course ->
        (levelFilter == "Все" || course.level == levelFilter) &&
        (query.isBlank() || course.title.contains(query, ignoreCase = true))
    }

    if (filteredCourses.isEmpty()) {
        Text(
            text = "Курсы не найдены",
            color = textColor.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    } else {
        filteredCourses.forEach { course ->
            CourseCardWithFavorite(
                db = db,
                course = course,
                login = login,
                isDark = isDark,
                onClick = { onCourseClick(course) },
                onFavoriteChanged = onFavoriteChanged
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CourseCardWithFavorite(
    db: LocalDatabase,
    course: Course,
    login: String?,
    isDark: Boolean,
    onClick: () -> Unit,
    onFavoriteChanged: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.12f) else Color.Black.copy(alpha = 0.08f)

    val levelColor = when (course.level) {
        "Начальный" -> SuccessGreen
        "Средний" -> AccentOrange
        else -> AccentPink
    }

    var isFavorite by remember { mutableStateOf(login != null && db.isFavorite(login, course.id)) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(levelColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = course.icon, fontSize = 26.sp)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = course.title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(levelColor.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(text = course.level, color = levelColor, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${course.lessonsCount} уроков",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
            }

            // Кнопка избранного
            if (login != null) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isFavorite) AccentOrange.copy(alpha = 0.2f) else Color.Transparent)
                        .clickable {
                            if (isFavorite) {
                                db.removeFromFavorites(login, course.id)
                            } else {
                                db.addToFavorites(login, course.id)
                            }
                            isFavorite = !isFavorite
                            onFavoriteChanged()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "⭐" else "☆",
                        fontSize = 20.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun FavoritesTabContent(
    courses: List<Course>,
    isDark: Boolean,
    onCourseClick: (Course) -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Text(
        text = "⭐ Избранные курсы",
        color = textColor,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(16.dp))

    if (courses.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "⭐", fontSize = 48.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Нет избранных курсов",
                color = textColor.copy(alpha = 0.5f),
                fontSize = 14.sp
            )
            Text(
                text = "Добавьте курсы в избранное,\nнажав на звёздочку",
                color = textColor.copy(alpha = 0.4f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    } else {
        courses.forEach { course ->
            CourseCardNew(course = course, isDark = isDark, onClick = { onCourseClick(course) })
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun CoursesTabContent(courses: List<Triple<String, String, String>>, isDark: Boolean = true) {
    var query by remember { mutableStateOf("") }
    var levelFilter by remember { mutableStateOf("Все") }
    var sortAsc by remember { mutableStateOf(true) }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.06f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f)

    // Поиск
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("🔍 Поиск курсов", color = textColor.copy(alpha = 0.6f)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GradientStart,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = GradientStart
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Фильтры по уровню
    val levels = listOf("Все", "Начальный", "Средний")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        levels.forEach { lvl ->
            val isSelected = levelFilter == lvl
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                        else Brush.linearGradient(listOf(bgColor, bgColor))
                    )
                    .clickable { levelFilter = lvl }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = lvl,
                    color = if (isSelected) Color.White else textColor.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Сортировка
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { sortAsc = !sortAsc }
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (sortAsc) "⬆️ Сортировка: А-Я" else "⬇️ Сортировка: Я-А",
            color = textColor.copy(alpha = 0.8f),
            fontSize = 13.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    val filtered = courses
        .filter { (title, desc, level) ->
            (query.isBlank() || title.contains(query, ignoreCase = true) || desc.contains(query, ignoreCase = true)) &&
                    (levelFilter == "Все" || level.equals(levelFilter, ignoreCase = true))
        }
        .sortedBy { it.first.lowercase() }
        .let { if (sortAsc) it else it.reversed() }

    // Карточки курсов
    filtered.forEach { (title, desc, level) ->
        CourseCard(title = title, description = desc, level = level, isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))
    }
}

@Composable
private fun CourseCard(title: String, description: String, level: String, isDark: Boolean = true) {
    val levelColor = when (level) {
        "Начальный" -> SuccessGreen
        "Средний" -> AccentOrange
        else -> AccentPink
    }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f))
                 else listOf(Color.Black.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.02f))
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = cardBg))
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🐍",
                    fontSize = 28.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(levelColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = level,
                        color = levelColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = description,
                color = textColor.copy(alpha = 0.6f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Начать обучение",
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
private fun ProfileTabContent(
    db: LocalDatabase,
    login: String?,
    isDark: Boolean = true,
    onLogout: () -> Unit = {},
    onOpenSupport: () -> Unit = {}
) {
    val context = LocalContext.current
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    if (login == null) {
        Text("Пользователь не определён", color = textColor)
        return
    }

    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var userLogin by remember { mutableStateOf(login) }
    var password by remember { mutableStateOf("") }
    var avatarPath by remember { mutableStateOf<String?>(null) }
    var avatarBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var loaded by remember { mutableStateOf(false) }

    // Launcher для выбора изображения из галереи
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            try {
                val inputStream = context.contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                inputStream?.close()
                
                // Сохраняем изображение в локальное хранилище
                val fileName = "avatar_${login}_${System.currentTimeMillis()}.jpg"
                val file = java.io.File(context.filesDir, fileName)
                val outputStream = java.io.FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
                outputStream.close()
                
                // Обновляем путь в БД
                db.updateUserAvatar(login, file.absolutePath)
                avatarPath = file.absolutePath
                avatarBitmap = bitmap
            } catch (e: Exception) {
                Log.e("Profile", "Error loading avatar: ${e.message}")
            }
        }
    }

    if (!loaded) {
        val cursor = db.readableDatabase.rawQuery(
            "SELECT first_name, last_name, email, password, avatar FROM users WHERE login = ?",
            arrayOf(login)
        )
        cursor.use {
            if (it.moveToFirst()) {
                firstName = it.getString(0) ?: ""
                lastName = it.getString(1) ?: ""
                email = it.getString(2) ?: ""
                password = it.getString(3) ?: ""
                avatarPath = it.getString(4)
            }
        }
        // Загружаем bitmap если есть путь к файлу
        avatarPath?.let { path ->
            if (path.startsWith("/")) {
                try {
                    val file = java.io.File(path)
                    if (file.exists()) {
                        avatarBitmap = BitmapFactory.decodeFile(path)
                    }
                } catch (e: Exception) {
                    Log.e("Profile", "Error loading avatar bitmap: ${e.message}")
                }
            }
        }
        loaded = true
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        // Аватар с возможностью загрузки из галереи
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                .clickable { imagePickerLauncher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (avatarBitmap != null) {
                Image(
                    bitmap = avatarBitmap!!.asImageBitmap(),
                    contentDescription = "Аватар",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop
                )
            } else {
                // Показываем первую букву имени если нет аватарки
                Text(
                    text = firstName.firstOrNull()?.uppercase() ?: login.firstOrNull()?.uppercase() ?: "?",
                    fontSize = 40.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Иконка камеры поверх
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(SecondaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "📷", fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Нажмите для загрузки фото",
            color = textColor.copy(alpha = 0.4f),
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "@$login",
            color = textColor.copy(alpha = 0.5f),
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        ModernTextField(value = firstName, onValueChange = { firstName = it }, label = "Имя", icon = "👤", isDark = isDark)
        Spacer(modifier = Modifier.height(10.dp))

        ModernTextField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия", icon = "👥", isDark = isDark)
        Spacer(modifier = Modifier.height(10.dp))

        ModernTextField(value = email, onValueChange = { email = it }, label = "Email", icon = "📧", isDark = isDark)
        Spacer(modifier = Modifier.height(10.dp))

        ModernTextField(value = userLogin, onValueChange = { userLogin = it }, label = "Логин", icon = "🔑", isDark = isDark)
        Spacer(modifier = Modifier.height(10.dp))

        ModernTextField(value = password, onValueChange = { password = it }, label = "Пароль", icon = "🔒", isPassword = true, isDark = isDark)
        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(
            text = "💾 Сохранить изменения",
            onClick = {
                // Обновляем данные пользователя включая логин
                db.writableDatabase.execSQL(
                    "UPDATE users SET first_name = ?, last_name = ?, email = ?, login = ?, password = ? WHERE login = ?",
                    arrayOf(firstName, lastName, email, userLogin, password, login)
                )
                // Обновляем связанные таблицы если логин изменился
                if (userLogin != login) {
                    db.writableDatabase.execSQL(
                        "UPDATE user_stats SET user_login = ? WHERE user_login = ?",
                        arrayOf(userLogin, login)
                    )
                    db.writableDatabase.execSQL(
                        "UPDATE favorites SET user_login = ? WHERE user_login = ?",
                        arrayOf(userLogin, login)
                    )
                }
                Log.d("Profile", "Profile updated for $login -> $userLogin")
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка технической поддержки
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SecondaryBlue.copy(alpha = 0.15f))
                .border(width = 1.dp, color = SecondaryBlue.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp))
                .clickable { onOpenSupport() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "💬 Техническая поддержка",
                color = SecondaryBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка выхода
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(cardBg)
                .border(width = 1.dp, color = AccentOrange.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp))
                .clickable { onLogout() },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🚪 Выйти из аккаунта",
                color = AccentOrange,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка удаления аккаунта
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(AccentPink.copy(alpha = 0.1f))
                .border(width = 1.dp, color = AccentPink.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp))
                .clickable { showDeleteConfirmation = true },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "🗑️ Удалить аккаунт",
                color = AccentPink,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }

    }

    // Всплывающий диалог подтверждения удаления
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = {
                Text(
                    text = "⚠️ Удаление аккаунта",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(text = "Вы точно хотите удалить свой аккаунт?")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Это действие нельзя отменить",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        db.writableDatabase.execSQL("DELETE FROM favorites WHERE user_login = ?", arrayOf(login))
                        db.writableDatabase.execSQL("DELETE FROM user_stats WHERE user_login = ?", arrayOf(login))
                        db.writableDatabase.execSQL("DELETE FROM users WHERE login = ?", arrayOf(login))
                        showDeleteConfirmation = false
                        onLogout()
                    }
                ) {
                    Text(text = "Да", color = AccentPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmation = false }) {
                    Text(text = "Нет")
                }
            }
        )
    }
}

@Composable
private fun GuestProfileContent(isDark: Boolean) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "👤", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Гостевой режим",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Зарегистрируйтесь, чтобы \nсохранять прогресс обучения",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun GuestCourseContent(isDark: Boolean, onBack: () -> Unit) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .align(Alignment.Start)
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(text = "📚", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Гостевой режим",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Зарегистрируйтесь, чтобы\nпройти данный курс",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatsTabContent(db: LocalDatabase, login: String?, isDark: Boolean = true) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    if (login == null) {
        Text("Пользователь не определён", color = textColor)
        return
    }

    var completed by remember { mutableStateOf(0) }
    var minutes by remember { mutableStateOf(0) }
    var totalCourses by remember { mutableStateOf(0) }
    var loaded by remember { mutableStateOf(false) }

    if (!loaded) {
        val statsCursor = db.readableDatabase.rawQuery(
            "SELECT completed_courses, total_time_minutes FROM user_stats WHERE user_login = ?",
            arrayOf(login)
        )
        statsCursor.use {
            if (it.moveToFirst()) {
                completed = it.getInt(0)
                minutes = it.getInt(1)
            } else {
                db.writableDatabase.execSQL(
                    "INSERT OR IGNORE INTO user_stats (user_login, completed_courses, total_time_minutes) VALUES (?, 0, 0)",
                    arrayOf(login)
                )
            }
        }

        val courseCursor = db.readableDatabase.rawQuery(
            "SELECT COUNT(*) FROM courses",
            null
        )
        courseCursor.use {
            if (it.moveToFirst()) {
                totalCourses = it.getInt(0)
            }
        }

        loaded = true
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "📊",
            fontSize = 48.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ваша статистика",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Карточки статистики
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "✅",
                value = "$completed/$totalCourses",
                label = "Курсов",
                color = SuccessGreen,
                isDark = isDark
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "⏱️",
                value = "$minutes",
                label = "Минут",
                color = SecondaryBlue,
                isDark = isDark
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "🔥",
                value = "0",
                label = "Дней подряд",
                color = AccentOrange,
                isDark = isDark
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = "⭐",
                value = "0",
                label = "Достижений",
                color = AccentPink,
                isDark = isDark
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Кнопка экспорта статистики
        val context = LocalContext.current
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(SecondaryBlue.copy(alpha = 0.2f))
                .border(width = 1.dp, color = SecondaryBlue.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp))
                .clickable {
                    val stats = db.getAllUsersStats().filter { it.login == login }
                    val file = ExcelExporter.exportStatsToCSV(context, stats)
                    file?.let { ExcelExporter.shareCSVFile(context, it) }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "📊 Экспорт моей статистики",
                color = SecondaryBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun GuestStatsContent(isDark: Boolean) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "📊", fontSize = 60.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Статистика недоступна",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Зарегистрируйтесь, чтобы \nотслеживать свой прогресс",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    icon: String,
    value: String,
    label: String,
    color: Color,
    isDark: Boolean = true
) {
    val labelColor = if (isDark) Color.White.copy(alpha = 0.6f) else Color(0xFF1A1A2E).copy(alpha = 0.6f)

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.3f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = icon, fontSize = 24.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                color = color,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = labelColor,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
fun AdminScreen(
    isDark: Boolean,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val db = remember { LocalDatabase(context) }
    var currentView by remember { mutableStateOf("menu") }
    var courses by remember { mutableStateOf(db.getAllCourses()) }
    var editingCourse by remember { mutableStateOf<Course?>(null) }

    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentView) {
            "menu" -> {
                // Админ бейдж
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Brush.linearGradient(listOf(AccentPink, AccentOrange))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👑", fontSize = 40.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Панель администратора",
                    color = textColor,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "@admin",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Стеклянная карточка
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(cardBg)
                        .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Column {
                        AdminMenuItem(
                            icon = "📚",
                            title = "Курсы",
                            subtitle = "Добавление, редактирование, удаление",
                            isDark = isDark,
                            onClick = { currentView = "courses" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AdminMenuItem(
                            icon = "👥",
                            title = "Пользователи",
                            subtitle = "Просмотр зарегистрированных",
                            isDark = isDark,
                            onClick = { currentView = "users" }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        AdminMenuItem(
                            icon = "💬",
                            title = "Вопросы пользователей",
                            subtitle = "Чаты технической поддержки",
                            isDark = isDark,
                            onClick = { currentView = "support" }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Кнопка выхода
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardBg)
                        .border(width = 1.dp, color = AccentPink.copy(alpha = 0.5f), shape = RoundedCornerShape(14.dp))
                        .clickable { onLogout() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Выйти из аккаунта", color = AccentPink, fontWeight = FontWeight.Medium)
                }
            }

            "courses" -> {
                AdminCoursesScreen(
                    db = db,
                    courses = courses,
                    isDark = isDark,
                    onBack = { currentView = "menu" },
                    onRefresh = { courses = db.getAllCourses() },
                    onEdit = { course ->
                        editingCourse = course
                        currentView = "editCourse"
                    }
                )
            }

            "editCourse" -> {
                AdminEditCourseScreen(
                    db = db,
                    course = editingCourse,
                    isDark = isDark,
                    onBack = {
                        editingCourse = null
                        courses = db.getAllCourses()
                        currentView = "courses"
                    }
                )
            }

            "users" -> {
                AdminUsersScreen(
                    db = db,
                    isDark = isDark,
                    onBack = { currentView = "menu" }
                )
            }

            "support" -> {
                AdminSupportScreen(
                    db = db,
                    isDark = isDark,
                    onBack = { currentView = "menu" }
                )
            }

        }
    }
}

@Composable
private fun AdminMenuItem(
    icon: String,
    title: String,
    subtitle: String,
    isDark: Boolean = true,
    onClick: () -> Unit = {}
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 24.sp)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = title,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = textColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

// ==================== НОВЫЕ КОМПОНЕНТЫ ====================

@Composable
private fun CoursesTabContentNew(
    courses: List<Course>,
    isDark: Boolean,
    onCourseClick: (Course) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var levelFilter by remember { mutableStateOf("Все") }
    var sortAZ by remember { mutableStateOf(false) }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val bgColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.06f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f)

    // Поиск
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth(),
        label = { Text("🔍 Поиск курсов", color = textColor.copy(alpha = 0.6f)) },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = GradientStart,
            unfocusedBorderColor = borderColor,
            focusedTextColor = textColor,
            unfocusedTextColor = textColor,
            cursorColor = GradientStart
        ),
        shape = RoundedCornerShape(14.dp),
        singleLine = true
    )

    Spacer(modifier = Modifier.height(12.dp))

    // Фильтры по уровню - кнопка "Все" сверху, остальные под ней
    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка "Все" на всю ширину
        val isAllSelected = levelFilter == "Все"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (isAllSelected) Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    else Brush.linearGradient(listOf(bgColor, bgColor))
                )
                .clickable { levelFilter = "Все" }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Все курсы",
                color = if (isAllSelected) Color.White else textColor.copy(alpha = 0.6f),
                fontSize = 13.sp,
                fontWeight = if (isAllSelected) FontWeight.Medium else FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Остальные кнопки в ряд
        val otherLevels = listOf("Начальный", "Средний", "Продвинутый")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            otherLevels.forEach { lvl ->
                val isSelected = levelFilter == lvl
                val lvlColor = when (lvl) {
                    "Начальный" -> SuccessGreen
                    "Средний" -> AccentOrange
                    else -> AccentPink
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) lvlColor.copy(alpha = 0.3f)
                            else bgColor
                        )
                        .border(
                            width = if (isSelected) 1.dp else 0.dp,
                            color = if (isSelected) lvlColor else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { levelFilter = lvl }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lvl,
                        color = if (isSelected) lvlColor else textColor.copy(alpha = 0.6f),
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Кнопка сортировки от А до Я
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (sortAZ) SecondaryBlue.copy(alpha = 0.3f)
                    else bgColor
                )
                .border(
                    width = if (sortAZ) 1.dp else 0.dp,
                    color = if (sortAZ) SecondaryBlue else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { sortAZ = !sortAZ }
                .padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (sortAZ) "🔤 Сортировка: А → Я ✓" else "🔤 Сортировка: А → Я",
                color = if (sortAZ) SecondaryBlue else textColor.copy(alpha = 0.6f),
                fontSize = 12.sp,
                fontWeight = if (sortAZ) FontWeight.Medium else FontWeight.Normal
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    val filtered = courses
        .filter { course ->
            (query.isBlank() || course.title.contains(query, ignoreCase = true) || course.description.contains(query, ignoreCase = true)) &&
                    (levelFilter == "Все" || course.level.equals(levelFilter, ignoreCase = true))
        }
        .let { list ->
            if (sortAZ) list.sortedBy { it.title.lowercase() } else list
        }

    // Карточки курсов
    filtered.forEach { course ->
        CourseCardNew(course = course, isDark = isDark, onClick = { onCourseClick(course) })
        Spacer(modifier = Modifier.height(12.dp))
    }

    if (filtered.isEmpty()) {
        Text(
            text = "Курсы не найдены",
            color = textColor.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CourseCardNew(course: Course, isDark: Boolean, onClick: () -> Unit) {
    val levelColor = when (course.level) {
        "Начальный" -> SuccessGreen
        "Средний" -> AccentOrange
        else -> AccentPink
    }
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) listOf(Color.White.copy(alpha = 0.08f), Color.White.copy(alpha = 0.04f))
    else listOf(Color.Black.copy(alpha = 0.04f), Color.Black.copy(alpha = 0.02f))
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.linearGradient(colors = cardBg))
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = course.icon, fontSize = 28.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(levelColor.copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = course.level,
                        color = levelColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = course.title,
                color = textColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = course.description,
                color = textColor.copy(alpha = 0.6f),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📖 ${course.lessonsCount} уроков",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd)))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "Открыть →",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CourseLessonsScreen(
    db: LocalDatabase,
    course: Course,
    login: String?,
    isDark: Boolean,
    onBack: () -> Unit,
    onLessonClick: (Lesson) -> Unit,
    onFavoriteChanged: () -> Unit = {}
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    val lessons = remember { db.getLessonsForCourse(course.id) }
    var isFavorite by remember { mutableStateOf(login != null && db.isFavorite(login, course.id)) }
    var completedCount by remember { mutableStateOf(if (login != null) db.getCompletedLessonsCount(login, course.id) else 0) }
    val totalLessons = lessons.size
    val progress = if (totalLessons > 0) completedCount.toFloat() / totalLessons else 0f
    val allCompleted = completedCount == totalLessons && totalLessons > 0
    var showTest by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Верхняя панель с кнопкой назад и звездой избранного
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = "← Назад к курсам", color = textColor, fontSize = 14.sp)
            }

            // Кнопка избранного
            if (login != null) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (isFavorite) AccentOrange.copy(alpha = 0.2f) else cardBg)
                        .clickable {
                            if (isFavorite) {
                                db.removeFromFavorites(login, course.id)
                            } else {
                                db.addToFavorites(login, course.id)
                            }
                            isFavorite = !isFavorite
                            onFavoriteChanged()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFavorite) "⭐" else "☆",
                        fontSize = 22.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Заголовок курса
        Text(
            text = course.icon,
            fontSize = 48.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = course.title,
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            text = course.description,
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Прогресс-бар прохождения курса
        if (login != null) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Прогресс",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "$completedCount / $totalLessons уроков",
                        color = textColor.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(cardBg)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        if (allCompleted) SuccessGreen else GradientStart,
                                        if (allCompleted) SuccessGreen else GradientEnd
                                    )
                                )
                            )
                    )
                }
                if (allCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "✅ Все уроки пройдены!",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка "Пройти тест" если все уроки пройдены
        if (allCompleted && login != null) {
            if (showTest) {
                CourseTestScreen(
                    course = course,
                    isDark = isDark,
                    onBack = { showTest = false },
                    onTestCompleted = { passed ->
                        showTest = false
                    }
                )
                return
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(listOf(SuccessGreen, SuccessGreen.copy(alpha = 0.8f))))
                    .clickable { showTest = true }
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "📝 Пройти тест",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Список уроков
        lessons.forEach { lesson ->
            val isCompleted = login != null && db.isLessonCompleted(login, lesson.id)
            LessonCardWithProgress(
                lesson = lesson,
                isDark = isDark,
                isCompleted = isCompleted,
                onClick = { onLessonClick(lesson) }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun LessonCard(lesson: Lesson, isDark: Boolean, onClick: () -> Unit) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Номер урока
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lesson.orderNum}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "⏱ ${lesson.durationMinutes} мин",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Text(text = "→", color = textColor.copy(alpha = 0.5f), fontSize = 18.sp)
        }
    }
}

@Composable
private fun LessonCardWithProgress(
    lesson: Lesson,
    isDark: Boolean,
    isCompleted: Boolean,
    onClick: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isCompleted) SuccessGreen.copy(alpha = 0.5f) else if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(if (isCompleted) SuccessGreen.copy(alpha = 0.1f) else cardBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompleted) Brush.linearGradient(listOf(SuccessGreen, SuccessGreen))
                        else Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isCompleted) "✓" else "${lesson.orderNum}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = lesson.title,
                    color = textColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = if (isCompleted) "✅ Пройден" else "⏱ ${lesson.durationMinutes} мин",
                    color = if (isCompleted) SuccessGreen else textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }

            Text(text = "→", color = textColor.copy(alpha = 0.5f), fontSize = 18.sp)
        }
    }
}

@Composable
private fun CourseTestScreen(
    course: Course,
    isDark: Boolean,
    onBack: () -> Unit,
    onTestCompleted: (Boolean) -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    val questions = listOf(
        DefaultTestQuestion("Что такое Python?", listOf("Язык программирования", "База данных", "Операционная система", "Браузер"), 0),
        DefaultTestQuestion("Какой символ используется для комментариев в Python?", listOf("//", "#", "/*", "--"), 1),
        DefaultTestQuestion("Как объявить переменную в Python?", listOf("var x = 5", "int x = 5", "x = 5", "let x = 5"), 2),
        DefaultTestQuestion("Какой тип данных для целых чисел?", listOf("float", "str", "int", "bool"), 2),
        DefaultTestQuestion("Как вывести текст в консоль?", listOf("console.log()", "print()", "echo()", "System.out.println()"), 1)
    )

    var currentQuestion by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var correctAnswers by remember { mutableStateOf(0) }
    var testFinished by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        val totalQuestions = questions.size
        val passThreshold = (totalQuestions * 0.6).toInt().coerceAtLeast(1)

        if (testFinished) {
            val passed = correctAnswers >= passThreshold
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (passed) "🎉" else "📚",
                    fontSize = 64.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (passed) "Тест пройден!" else "Попробуйте ещё раз",
                    color = if (passed) SuccessGreen else AccentOrange,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Правильных ответов: $correctAnswers из $totalQuestions",
                    color = textColor.copy(alpha = 0.7f),
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                GradientButton(
                    text = "Готово",
                    onClick = { onTestCompleted(passed) }
                )
            }
        } else {
            Text(
                text = "📝 Тест: ${course.title}",
                color = textColor,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Вопрос ${currentQuestion + 1} из $totalQuestions",
                color = textColor.copy(alpha = 0.6f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            val question = questions[currentQuestion]

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .padding(16.dp)
            ) {
                Text(
                    text = question.text,
                    color = textColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            question.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isSelected) SecondaryBlue.copy(alpha = 0.2f) else cardBg
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) SecondaryBlue else if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { selectedAnswer = index }
                        .padding(16.dp)
                ) {
                    Text(
                        text = option,
                        color = textColor,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (selectedAnswer >= 0) {
                GradientButton(
                    text = if (currentQuestion < totalQuestions - 1) "Следующий вопрос" else "Завершить тест",
                    onClick = {
                        if (selectedAnswer == question.correctIndex) {
                            correctAnswers++
                        }
                        if (currentQuestion < totalQuestions - 1) {
                            currentQuestion++
                            selectedAnswer = -1
                        } else {
                            testFinished = true
                        }
                    }
                )
            }
        }
    }
}

private data class DefaultTestQuestion(
    val text: String,
    val options: List<String>,
    val correctIndex: Int
)

@Composable
private fun LessonDetailScreen(
    lesson: Lesson,
    isDark: Boolean,
    onBack: () -> Unit,
    onComplete: () -> Unit = {}
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад к урокам", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Номер и название урока
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${lesson.orderNum}",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Урок ${lesson.orderNum}",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
                Text(
                    text = lesson.title,
                    color = textColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "⏱ ${lesson.durationMinutes} минут",
            color = SecondaryBlue,
            fontSize = 13.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Контент урока
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .padding(20.dp)
        ) {
            Text(
                text = lesson.content,
                color = textColor,
                fontSize = 15.sp,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Кнопка завершения
        GradientButton(
            text = "✓ Урок завершён",
            onClick = {
                onComplete()
                onBack()
            }
        )
    }
}

// ==================== АДМИН ЭКРАНЫ ====================

@Composable
private fun AdminCoursesScreen(
    db: LocalDatabase,
    courses: List<Course>,
    isDark: Boolean,
    onBack: () -> Unit,
    onRefresh: () -> Unit,
    onEdit: (Course) -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "📚 Управление курсами",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Всего курсов: ${courses.size}",
            color = textColor.copy(alpha = 0.5f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка добавления
        GradientButton(
            text = "+ Добавить курс",
            onClick = { onEdit(Course(0, "", "", "Начальный", "🐍", 0)) }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Список курсов
        courses.forEach { course ->
            AdminCourseItem(
                course = course,
                isDark = isDark,
                onEdit = { onEdit(course) },
                onDelete = {
                    db.deleteCourse(course.id)
                    onRefresh()
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun AdminCourseItem(
    course: Course,
    isDark: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    val levelColor = when (course.level) {
        "Начальный" -> SuccessGreen
        "Средний" -> AccentOrange
        else -> AccentPink
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = course.icon, fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = course.title,
                            color = textColor,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "${course.level} • ${course.lessonsCount} уроков",
                            color = levelColor,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Кнопка редактирования
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(SecondaryBlue.copy(alpha = 0.2f))
                        .clickable { onEdit() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "✏️ Редактировать", color = SecondaryBlue, fontSize = 13.sp)
                }

                // Кнопка удаления
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AccentPink.copy(alpha = 0.2f))
                        .clickable { onDelete() }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🗑️ Удалить", color = AccentPink, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminEditCourseScreen(
    db: LocalDatabase,
    course: Course?,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var title by remember { mutableStateOf(course?.title ?: "") }
    var description by remember { mutableStateOf(course?.description ?: "") }
    var level by remember { mutableStateOf(course?.level ?: "Начальный") }
    val isNew = course?.id == 0
    
    var showLessons by remember { mutableStateOf(false) }
    var lessons by remember { mutableStateOf(if (course != null && course.id != 0) db.getLessonsForCourse(course.id) else emptyList()) }
    var editingLesson by remember { mutableStateOf<Lesson?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Отмена", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isNew) "➕ Новый курс" else "✏️ Редактирование",
            color = textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernTextField(
            value = title,
            onValueChange = { title = it },
            label = "Название курса",
            icon = "📚",
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(
            value = description,
            onValueChange = { description = it },
            label = "Описание",
            icon = "📝",
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Уровень сложности",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Выбор уровня
        val levels = listOf("Начальный", "Средний", "Продвинутый")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            levels.forEach { lvl ->
                val isSelected = level == lvl
                val lvlColor = when (lvl) {
                    "Начальный" -> SuccessGreen
                    "Средний" -> AccentOrange
                    else -> AccentPink
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) lvlColor.copy(alpha = 0.3f) else cardBg)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) lvlColor else textColor.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickable { level = lvl }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = lvl,
                        color = if (isSelected) lvlColor else textColor.copy(alpha = 0.6f),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        GradientButton(
            text = if (isNew) "✓ Создать курс" else "✓ Сохранить",
            onClick = {
                if (title.isNotBlank() && description.isNotBlank()) {
                    if (isNew) {
                        db.addCourse(title, description, level)
                    } else {
                        db.updateCourse(course!!.id, title, description, level)
                    }
                    onBack()
                }
            }
        )

        // Управление уроками (только для существующих курсов)
        if (!isNew && course != null) {
            Spacer(modifier = Modifier.height(24.dp))

            // Разделитель
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(borderColor)
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (editingLesson != null) {
                // Экран редактирования урока
                AdminEditLessonScreen(
                    db = db,
                    courseId = course.id,
                    lesson = editingLesson,
                    isDark = isDark,
                    onBack = {
                        editingLesson = null
                        lessons = db.getLessonsForCourse(course.id)
                    }
                )
            } else if (showLessons) {
                // Список уроков
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "📖 Уроки курса (${lessons.size})",
                        color = textColor,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(cardBg)
                            .clickable { showLessons = false }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = "Свернуть", color = textColor.copy(alpha = 0.6f), fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Кнопка добавления урока
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SuccessGreen.copy(alpha = 0.2f))
                        .border(width = 1.dp, color = SuccessGreen.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                        .clickable { editingLesson = Lesson(0, course.id, "", "", lessons.size + 1, 10) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "+ Добавить урок", color = SuccessGreen, fontWeight = FontWeight.Medium)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Список уроков
                lessons.forEach { lesson ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(cardBg)
                            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${lesson.orderNum}",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = lesson.title,
                                        color = textColor,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "⏱ ${lesson.durationMinutes} мин",
                                        color = textColor.copy(alpha = 0.5f),
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(SecondaryBlue.copy(alpha = 0.2f))
                                        .clickable { editingLesson = lesson }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✏️ Изменить", color = SecondaryBlue, fontSize = 11.sp)
                                }
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AccentPink.copy(alpha = 0.2f))
                                        .clickable {
                                            db.deleteLesson(lesson.id)
                                            lessons = db.getLessonsForCourse(course.id)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🗑️ Удалить", color = AccentPink, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (lessons.isEmpty()) {
                    Text(
                        text = "Уроков пока нет",
                        color = textColor.copy(alpha = 0.5f),
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                // Кнопка показа уроков
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(SecondaryBlue.copy(alpha = 0.2f))
                        .border(width = 1.dp, color = SecondaryBlue.copy(alpha = 0.5f), shape = RoundedCornerShape(12.dp))
                        .clickable { showLessons = true }
                        .padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "📖 Управление уроками (${lessons.size})",
                        color = SecondaryBlue,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun AdminEditLessonScreen(
    db: LocalDatabase,
    courseId: Int,
    lesson: Lesson?,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    var lessonTitle by remember { mutableStateOf(lesson?.title ?: "") }
    var lessonContent by remember { mutableStateOf(lesson?.content ?: "") }
    var duration by remember { mutableStateOf((lesson?.durationMinutes ?: 10).toString()) }
    val isNewLesson = lesson?.id == 0

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад к урокам", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isNewLesson) "➕ Новый урок" else "✏️ Редактирование урока",
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        ModernTextField(
            value = lessonTitle,
            onValueChange = { lessonTitle = it },
            label = "Название урока",
            icon = "📖",
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(
            value = duration,
            onValueChange = { duration = it.filter { c -> c.isDigit() } },
            label = "Длительность (минут)",
            icon = "⏱",
            isDark = isDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Поле для контента урока
        Text(
            text = "📝 Содержание урока",
            color = textColor.copy(alpha = 0.6f),
            fontSize = 14.sp
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = lessonContent,
            onValueChange = { lessonContent = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GradientStart,
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.15f),
                focusedTextColor = textColor,
                unfocusedTextColor = textColor,
                cursorColor = GradientStart
            ),
            shape = RoundedCornerShape(14.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        GradientButton(
            text = if (isNewLesson) "✓ Создать урок" else "✓ Сохранить",
            onClick = {
                if (lessonTitle.isNotBlank() && lessonContent.isNotBlank()) {
                    val durationInt = duration.toIntOrNull() ?: 10
                    if (isNewLesson) {
                        db.addLesson(courseId, lessonTitle, lessonContent, lesson?.orderNum ?: 1, durationInt)
                    } else {
                        db.updateLesson(lesson!!.id, lessonTitle, lessonContent, durationInt)
                    }
                    onBack()
                }
            }
        )
    }
}

@Composable
private fun AdminUsersScreen(
    db: LocalDatabase,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var users by remember { mutableStateOf(db.getAllUsersStats()) }
    var editingUser by remember { mutableStateOf<UserStats?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (editingUser != null) {
            AdminEditUserScreen(
                db = db,
                user = editingUser!!,
                isDark = isDark,
                onBack = {
                    editingUser = null
                    users = db.getAllUsersStats()
                }
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = "← Назад", color = textColor, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "👥 Пользователи",
                color = textColor,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Зарегистрировано: ${users.size}",
                color = textColor.copy(alpha = 0.5f),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (users.isEmpty()) {
                Text(
                    text = "Пользователей пока нет",
                    color = textColor.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                users.forEach { user ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(45.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = user.firstName.firstOrNull()?.uppercase() ?: "?",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp
                                    )
                                }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${user.firstName} ${user.lastName}",
                                        color = textColor,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "@${user.login}",
                                        color = textColor.copy(alpha = 0.5f),
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = user.email,
                                        color = textColor.copy(alpha = 0.4f),
                                        fontSize = 11.sp
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "✅ ${user.completedCourses}",
                                        color = SuccessGreen,
                                        fontSize = 12.sp
                                    )
                                    Text(
                                        text = "⏱ ${user.totalTimeMinutes} мин",
                                        color = SecondaryBlue,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SecondaryBlue.copy(alpha = 0.2f))
                                        .clickable { editingUser = user }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "✏️ Редактировать", color = SecondaryBlue, fontSize = 12.sp)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(AccentPink.copy(alpha = 0.2f))
                                        .clickable {
                                            db.writableDatabase.execSQL("DELETE FROM user_stats WHERE user_login = ?", arrayOf(user.login))
                                            db.writableDatabase.execSQL("DELETE FROM users WHERE login = ?", arrayOf(user.login))
                                            users = db.getAllUsersStats()
                                        }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🗑️ Удалить", color = AccentPink, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminEditUserScreen(
    db: LocalDatabase,
    user: UserStats,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)

    var firstName by remember { mutableStateOf(user.firstName) }
    var lastName by remember { mutableStateOf(user.lastName) }
    var email by remember { mutableStateOf(user.email) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Отмена", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "✏️ Редактирование пользователя",
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "@${user.login}",
            color = textColor.copy(alpha = 0.5f),
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(20.dp))

        ModernTextField(value = firstName, onValueChange = { firstName = it }, label = "Имя", icon = "👤", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = lastName, onValueChange = { lastName = it }, label = "Фамилия", icon = "👥", isDark = isDark)
        Spacer(modifier = Modifier.height(12.dp))

        ModernTextField(value = email, onValueChange = { email = it }, label = "Email", icon = "📧", isDark = isDark)
        Spacer(modifier = Modifier.height(20.dp))

        GradientButton(
            text = "✓ Сохранить изменения",
            onClick = {
                if (firstName.isNotBlank() && lastName.isNotBlank() && email.isNotBlank()) {
                    db.writableDatabase.execSQL(
                        "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE login = ?",
                        arrayOf(firstName, lastName, email, user.login)
                    )
                    onBack()
                }
            }
        )
    }
}

// ==================== ТЕХНИЧЕСКАЯ ПОДДЕРЖКА ====================

@Composable
private fun SupportChatScreen(
    db: LocalDatabase,
    userLogin: String,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(db.getSupportMessages(userLogin)) }
    
    // Для автоскролла к последнему сообщению
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Адаптивная высота в зависимости от ориентации экрана
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val chatHeight = if (isLandscape) 200.dp else 400.dp
    
    // Автоскролл при изменении списка сообщений
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад к профилю", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 20.dp))

        // Заголовок
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isLandscape) 40.dp else 50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(SecondaryBlue, SecondaryBlue.copy(alpha = 0.7f)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "💬", fontSize = if (isLandscape) 18.sp else 24.sp)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Техническая поддержка",
                    color = textColor,
                    fontSize = if (isLandscape) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${messages.size} сообщений",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 20.dp))

        // Область сообщений - адаптивная высота
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chatHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "💬", fontSize = if (isLandscape) 32.sp else 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Нет сообщений",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Напишите первое сообщение!",
                        color = textColor.copy(alpha = 0.4f),
                        fontSize = 12.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    messages.forEach { message ->
                        ChatMessageBubble(
                            message = message,
                            isDark = isDark,
                            isCurrentUser = !message.isFromAdmin
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))

        // Поле ввода сообщения
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Введите сообщение...", color = textColor.copy(alpha = 0.4f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SecondaryBlue,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = SecondaryBlue
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (messageText.isNotBlank())
                            Brush.linearGradient(listOf(SecondaryBlue, SecondaryBlue.copy(alpha = 0.8f)))
                        else
                            Brush.linearGradient(listOf(cardBg, cardBg))
                    )
                    .clickable(enabled = messageText.isNotBlank()) {
                        if (messageText.isNotBlank()) {
                            db.sendSupportMessage(userLogin, messageText, isFromAdmin = false)
                            messageText = ""
                            messages = db.getSupportMessages(userLogin)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "➤",
                    color = if (messageText.isNotBlank()) Color.White else textColor.copy(alpha = 0.3f),
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    message: SupportMessage,
    isDark: Boolean,
    isCurrentUser: Boolean
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isCurrentUser) 16.dp else 4.dp,
                        bottomEnd = if (isCurrentUser) 4.dp else 16.dp
                    )
                )
                .background(
                    brush = if (isCurrentUser)
                        Brush.linearGradient(listOf(GradientStart, GradientEnd))
                    else
                        Brush.linearGradient(listOf(
                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f),
                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)
                        ))
                )
                .padding(12.dp)
        ) {
            Column {
                if (!isCurrentUser) {
                    Text(
                        text = "👑 Администратор",
                        color = AccentOrange,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.message,
                    color = if (isCurrentUser) Color.White else textColor,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.createdAt.takeLast(8).take(5),
                    color = if (isCurrentUser) Color.White.copy(alpha = 0.7f) else textColor.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}

// ==================== АДМИН: ВОПРОСЫ ПОЛЬЗОВАТЕЛЕЙ ====================

@Composable
private fun AdminSupportScreen(
    db: LocalDatabase,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var usersWithMessages by remember { mutableStateOf(db.getUsersWithMessages()) }
    var selectedUserLogin by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        if (selectedUserLogin != null) {
            // Чат с конкретным пользователем
            AdminChatWithUserScreen(
                db = db,
                userLogin = selectedUserLogin!!,
                isDark = isDark,
                onBack = {
                    selectedUserLogin = null
                    usersWithMessages = db.getUsersWithMessages()
                }
            )
        } else {
            // Список пользователей с сообщениями
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(cardBg)
                    .clickable { onBack() }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(text = "← Назад", color = textColor, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(SecondaryBlue, SecondaryBlue.copy(alpha = 0.7f)))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "💬", fontSize = 24.sp)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Вопросы пользователей",
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Всего чатов: ${usersWithMessages.size}",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (usersWithMessages.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "📭", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Нет сообщений",
                            color = textColor.copy(alpha = 0.5f),
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Пользователи ещё не писали в поддержку",
                            color = textColor.copy(alpha = 0.4f),
                            fontSize = 12.sp
                        )
                    }
                }
            } else {
                usersWithMessages.forEach { userLogin ->
                    val messages = db.getSupportMessages(userLogin)
                    val lastMessage = messages.lastOrNull()
                    val unreadCount = messages.count { !it.isFromAdmin }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(cardBg)
                            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                            .clickable { selectedUserLogin = userLogin }
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(45.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userLogin.firstOrNull()?.uppercase() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "@$userLogin",
                                    color = textColor,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                if (lastMessage != null) {
                                    Text(
                                        text = if (lastMessage.isFromAdmin) "Вы: ${lastMessage.message}" else lastMessage.message,
                                        color = textColor.copy(alpha = 0.5f),
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                if (unreadCount > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(SecondaryBlue),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "$unreadCount",
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Text(
                                    text = "${messages.size} сообщ.",
                                    color = textColor.copy(alpha = 0.4f),
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}

@Composable
private fun AdminChatWithUserScreen(
    db: LocalDatabase,
    userLogin: String,
    isDark: Boolean,
    onBack: () -> Unit
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)
    val cardBg = if (isDark) Color.White.copy(alpha = 0.06f) else Color.Black.copy(alpha = 0.04f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)

    var messageText by remember { mutableStateOf("") }
    var messages by remember { mutableStateOf(db.getSupportMessages(userLogin)) }
    
    // Для автоскролла к последнему сообщению
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    
    // Адаптивная высота в зависимости от ориентации экрана
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    val chatHeight = if (isLandscape) 200.dp else 400.dp
    
    // Автоскролл при изменении списка сообщений
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Кнопка назад
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(cardBg)
                .clickable { onBack() }
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            Text(text = "← Назад к чатам", color = textColor, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 20.dp))

        // Заголовок с информацией о пользователе
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(if (isLandscape) 40.dp else 50.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(GradientStart, GradientEnd))),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userLogin.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = if (isLandscape) 16.sp else 20.sp
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Чат с @$userLogin",
                    color = textColor,
                    fontSize = if (isLandscape) 16.sp else 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${messages.size} сообщений",
                    color = textColor.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 20.dp))

        // Область сообщений - адаптивная высота
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(chatHeight)
                .clip(RoundedCornerShape(16.dp))
                .background(cardBg)
                .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(16.dp))
                .padding(12.dp)
        ) {
            if (messages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "💬", fontSize = if (isLandscape) 32.sp else 48.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Нет сообщений",
                        color = textColor.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    messages.forEach { message ->
                        AdminChatMessageBubble(
                            message = message,
                            isDark = isDark,
                            isFromAdmin = message.isFromAdmin,
                            userLogin = userLogin
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscape) 8.dp else 12.dp))

        // Поле ввода ответа
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ответить пользователю...", color = textColor.copy(alpha = 0.4f)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AccentOrange,
                    unfocusedBorderColor = borderColor,
                    focusedTextColor = textColor,
                    unfocusedTextColor = textColor,
                    cursorColor = AccentOrange
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(
                        if (messageText.isNotBlank())
                            Brush.linearGradient(listOf(AccentOrange, AccentPink))
                        else
                            Brush.linearGradient(listOf(cardBg, cardBg))
                    )
                    .clickable(enabled = messageText.isNotBlank()) {
                        if (messageText.isNotBlank()) {
                            db.sendSupportMessage(userLogin, messageText, isFromAdmin = true)
                            messageText = ""
                            messages = db.getSupportMessages(userLogin)
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "➤",
                    color = if (messageText.isNotBlank()) Color.White else textColor.copy(alpha = 0.3f),
                    fontSize = 20.sp
                )
            }
        }
    }
}

@Composable
private fun AdminChatMessageBubble(
    message: SupportMessage,
    isDark: Boolean,
    isFromAdmin: Boolean,
    userLogin: String
) {
    val textColor = if (isDark) Color.White else Color(0xFF1A1A2E)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isFromAdmin) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isFromAdmin) 16.dp else 4.dp,
                        bottomEnd = if (isFromAdmin) 4.dp else 16.dp
                    )
                )
                .background(
                    brush = if (isFromAdmin)
                        Brush.linearGradient(listOf(AccentOrange, AccentPink))
                    else
                        Brush.linearGradient(listOf(
                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f),
                            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.08f)
                        ))
                )
                .padding(12.dp)
        ) {
            Column {
                if (!isFromAdmin) {
                    Text(
                        text = "👤 @$userLogin",
                        color = SecondaryBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                } else {
                    Text(
                        text = "👑 Вы (Администратор)",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
                Text(
                    text = message.message,
                    color = if (isFromAdmin) Color.White else textColor,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = message.createdAt.takeLast(8).take(5),
                    color = if (isFromAdmin) Color.White.copy(alpha = 0.7f) else textColor.copy(alpha = 0.5f),
                    fontSize = 10.sp
                )
            }
        }
    }
}
