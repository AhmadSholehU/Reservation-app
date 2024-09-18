package com.overdevx.reservationapp.data.presentation.monitoring.auth

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource

@Composable
fun LoginScreen(viewModel: AuthViewModel= hiltViewModel(), onLoginClick:()->Unit, navController: NavController, modifier: Modifier = Modifier) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsState()
    var hasShownDialog by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    // Handle back press to exit app
    BackHandler {
        // Menutup aplikasi jika berada di layar login dan menekan tombol back
        (navController.context as? Activity)?.finish()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Text(
            text = "Sign in \nto continue",
            color = secondary,
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 45.sp,
            lineHeight = 55.sp,
            modifier = Modifier,
            )
        Spacer(modifier = Modifier.height(50.dp))

        Image(
            painter = painterResource(id = R.drawable.img_smg),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Login",
            color = secondary,
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 24.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),

            )
        Spacer(modifier = Modifier.height(25.dp))
        Row(
            modifier = Modifier

                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(secondary)
                .padding(5.dp)
        )
        {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_email),
                contentDescription = null,
                tint = white,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(5.dp))
            BasicTextField(
                value = email,
                onValueChange = {
                    viewModel.onEmailChange(it)
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                    fontSize = 14.sp,
                    color = white,
                ),
                modifier = Modifier
                    .padding(start = 5.dp, end = 16.dp)
                    .align(Alignment.CenterVertically),
                cursorBrush = SolidColor(white),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier

                    ) {
                        if (email.isEmpty()) {
                            Text(
                                text = "Email",
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                                    fontSize = 14.sp,
                                    color = white2
                                )
                            )
                        }
                        innerTextField() // Tampilkan BasicTextField
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier

                .fillMaxWidth()
                .height(55.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(secondary)
                .padding(5.dp)
        )
        {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_lock),
                contentDescription = null,
                tint = white,
                modifier = Modifier
                    .size(20.dp)
                    .align(Alignment.CenterVertically)
            )
            Spacer(modifier = Modifier.width(5.dp))
            BasicTextField(
                value = password,
                onValueChange = {
                    viewModel.onPasswordChange(it)
                },
                textStyle = TextStyle(
                    fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                    fontSize = 14.sp,
                    color = white,
                ),
                modifier = Modifier
                    .padding(start = 5.dp, end = 16.dp)
                    .align(Alignment.CenterVertically),
                cursorBrush = SolidColor(white),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier

                    ) {
                        if (password.isEmpty()) {
                            Text(
                                text = "Password",
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                                    fontSize = 14.sp,
                                    color = white2
                                )
                            )
                        }
                        innerTextField() // Tampilkan BasicTextField
                    }
                }
            )
            Spacer(modifier = Modifier.weight(1f))
            IconButton(
                onClick = {
                    passwordVisible = !passwordVisible
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(40.dp),
                colors = IconButtonDefaults.iconButtonColors(Color.Transparent)
            ) {
                Icon(
                    painter = painterResource(
                        id = if (passwordVisible) R.drawable.ic_eye else R.drawable.ic_eye_slash
                    ),
                    contentDescription = null,
                    tint = white
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {
                viewModel.login()
            },
            modifier = Modifier
                .height(55.dp)
                .fillMaxWidth()
                .align(Alignment.End),
            colors = ButtonDefaults.buttonColors(primary),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text(
                text = "Login",
                fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                fontSize = 20.sp,
                color = white,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(modifier = Modifier) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Not have an account ?",
                color = secondary,
                fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
                fontSize = 16.sp,
                lineHeight = 55.sp,
                modifier = Modifier,
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = "Register",
                color = primary,
                fontFamily = FontFamily(listOf(Font(R.font.inter_bold))),
                fontSize = 16.sp,
                lineHeight = 55.sp,
                modifier = Modifier
                    .clickable { },
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        if(showDialog){
            AlertDialog(
                onDismissRequest = {

                }, // Menutup dialog saat di luar dialog ditekan
                title = {
                    Column (modifier = Modifier.fillMaxWidth()){
                        Icon(
                            painter = painterResource(id = R.drawable.ic_success),
                            contentDescription = null,
                            tint = primary,
                            modifier = Modifier
                                .size(55.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }

                },
                text = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Success",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 24.sp,
                            color = white,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your Login is successfully",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 16.sp,
                            color = white2,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            onLoginClick()
                        },
                        modifier = Modifier
                            .height(55.dp)
                            .fillMaxWidth()
                            .align(Alignment.End),
                        colors = ButtonDefaults.buttonColors(primary),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = "Okay",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 20.sp,
                            color = white,
                        )
                    }

                },
                containerColor = secondary
            )
        }

        when (loginState) {
            is Resource.Idle -> {

            }

            is Resource.Loading -> {
                // Tampilkan indikator loading
                CircularProgressIndicator(
                    color = primary,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is Resource.Success -> {
                // Tampilkan dialog hanya sekali
                if (!hasShownDialog) {
                    showDialog = true
                    hasShownDialog = true
                    viewModel.resetLoginState()
                }
            }

            is Resource.Error -> {
                // Tampilkan pesan error
                Text(
                    text = "Login failed: ${(loginState as Resource.Error).exception.message}",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is Resource.ErrorMessage -> {
                // Tampilkan pesan error dari Resource.ErrorMessage
                Text(
                    text = (loginState as Resource.ErrorMessage).message,
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}