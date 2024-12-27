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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.overdevx.reservationapp.R
import com.overdevx.reservationapp.data.presentation.monitoring.admin.Loading
import com.overdevx.reservationapp.data.presentation.monitoring.admin.LoadingDialog
import com.overdevx.reservationapp.ui.theme.primary
import com.overdevx.reservationapp.ui.theme.secondary
import com.overdevx.reservationapp.ui.theme.white
import com.overdevx.reservationapp.ui.theme.white2
import com.overdevx.reservationapp.utils.Resource

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }

    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    var hasShownDialog by remember { mutableStateOf(false) }

    var passwordVisible by remember { mutableStateOf(false) }

    // Handle back press to exit app

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(30.dp))
        Image(
            painter = painterResource(id = R.drawable.img_smg),
            contentDescription = null,
            modifier = Modifier
                .size(150.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "ASRAMA BALAI DIKLAT",
            color = secondary,
            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
            fontSize = 25.sp,
            textAlign = TextAlign.Center,
            letterSpacing = 3.sp,
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
        )
        Text(
            text = "Kota Semarang",
            color = secondary,
            fontFamily = FontFamily(listOf(Font(R.font.inter_medium))),
            fontSize = 20.sp,
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
                                text = "Masukan Email Anda",
                                style = TextStyle(
                                    fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                                    fontSize = 14.sp,
                                    color = white2
                                ),
                                modifier = Modifier.fillMaxWidth()
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
                    Row(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            if (password.isEmpty()) {
                                Text(
                                    text = "Masukan Kata Sandi",
                                    style = TextStyle(
                                        fontFamily = FontFamily(listOf(Font(R.font.inter_regular))),
                                        fontSize = 14.sp,
                                        color = white2
                                    ),
                                    modifier = Modifier
                                )
                            }
                            innerTextField()
                        }
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
                }
            )

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



        if (showDialog) {
            AlertDialog(
                onDismissRequest = {

                }, // Menutup dialog saat di luar dialog ditekan
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
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

        if(showErrorDialog){
            AlertDialog(
                onDismissRequest = {

                }, // Menutup dialog saat di luar dialog ditekan
                title = {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_circle_error),
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
                            text = "Failed",
                            fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                            fontSize = 24.sp,
                            color = white,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Failed to login",
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
                            showErrorDialog = false
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
              LoadingDialog {  }
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
                Text(
                    text = "Login failed: ${(loginState as Resource.Error).exception.message}",
                    color = Color.Blue,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )

            }

            is Resource.ErrorMessage -> {
                // Tampilkan pesan error dari Resource.ErrorMessage
                showErrorDialog=true
                viewModel.resetLoginState()
            }
        }
    }
}

@Composable
fun ErrorDialog(
    title: String,
    desc:String,
    onDismiss: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = {
onDismiss()
        }, // Menutup dialog saat di luar dialog ditekan
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_error),
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
                    text = title,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 24.sp,
                    color = white,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = desc,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = white2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                   onClick()
                    onDismiss()
                    showDialog = false
                },
                modifier = Modifier
                    .height(55.dp)
                    .fillMaxWidth(),
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

@Composable
fun ErrorDialogUnauthorized(
    title: String,
    desc:String,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {
    var showDialog by remember { mutableStateOf(true) }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        }, // Menutup dialog saat di luar dialog ditekan
        title = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_circle_error),
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
                    text = title,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 24.sp,
                    color = white,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = desc,
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 16.sp,
                    color = white2,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onClick()
                    onDismissRequest()
                    showDialog = false
                },
                modifier = Modifier
                    .height(55.dp)
                    .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(primary),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Login Kembali",
                    fontFamily = FontFamily(listOf(Font(R.font.inter_semibold))),
                    fontSize = 20.sp,
                    color = white,
                )
            }

        },
        containerColor = secondary
    )
}