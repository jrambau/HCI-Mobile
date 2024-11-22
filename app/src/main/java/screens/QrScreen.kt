import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lupay.MyApplication
import com.example.lupay.R
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import viewmodels.QrViewModel
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRScreen(
    navController: NavController,
    viewModel: QrViewModel = viewModel(factory = QrViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var isCameraActive by remember { mutableStateOf(true) }
    var showTransferDialog by remember { mutableStateOf(false) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    val userEmail by viewModel.userEmail.collectAsState()
    val cards by viewModel.cards.collectAsState()
    val transferResult by viewModel.transferResult.collectAsState()

    LaunchedEffect(key1 = Unit) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.qr_scanner)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = stringResource(id = R.string.back))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (hasCameraPermission && isCameraActive) {
                CameraPreview(
                    onQrCodeScanned = { result ->
                        scannedResult = result
                        showTransferDialog = true
                        isCameraActive = false
                    }
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.Center)
                        .border(2.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(16.dp))
                )
            } else {
                userEmail?.let { email ->
                    val bitmap = generateQRCode(email)
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.qr_code),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Button(
                onClick = { isCameraActive = !isCameraActive },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(if (isCameraActive) stringResource(id = R.string.show_my_qr) else stringResource(id = R.string.scan_qr))
            }
        }

        if (showTransferDialog) {
            TransferDialog(
                receiverEmail = scannedResult ?: "",
                onDismiss = {
                    showTransferDialog = false
                    isCameraActive = true
                    scannedResult = null
                },
                viewModel = viewModel
            )
        }

        transferResult?.let { result ->
            AlertDialog(
                onDismissRequest = { viewModel.clearTransferResult() },
                title = { Text(if (result.success) stringResource(R.string.transfer_successful) else stringResource(R.string.error_transfering_money)) },
                text = { Text(result.message) },
                confirmButton = {
                    TextButton(onClick = { viewModel.clearTransferResult() }) {
                        Text(stringResource(R.string.ok))
                    }
                },
                containerColor = MaterialTheme.colorScheme.surface,
                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



@SuppressLint("StateFlowValueCalledInComposition")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransferDialog(
    receiverEmail: String,
    onDismiss: () -> Unit,
    viewModel: QrViewModel
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.tranfer),
                    style = MaterialTheme.typography.titleLarge
                )
                OutlinedTextField(
                    value = receiverEmail,
                    onValueChange = { },
                    label = { Text(stringResource(id = R.string.dest_mail)) },
                    readOnly = true
                )
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text(stringResource(id = R.string.amount)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.desc)) }
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = { viewModel.setPaymentMethod(QrViewModel.PaymentMethod.WALLET) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedPaymentMethod == QrViewModel.PaymentMethod.WALLET)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(id = R.string.account))
                    }
                    Button(
                        onClick = { viewModel.setPaymentMethod(QrViewModel.PaymentMethod.CARD) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (viewModel.selectedPaymentMethod == QrViewModel.PaymentMethod.CARD)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(stringResource(id = R.string.card))
                    }
                }
                if (viewModel.selectedPaymentMethod == QrViewModel.PaymentMethod.CARD) {
                    var expanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = expanded,
                        onExpandedChange = { expanded = !expanded }
                    ) {
                        OutlinedTextField(
                            value = viewModel.selectedCard?.number ?: stringResource(id = R.string.select_card),
                            onValueChange = { },
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            viewModel.cards.value.forEach { card ->
                                DropdownMenuItem(
                                    text = { Text(card.number) },
                                    onClick = {
                                        viewModel.updateSelectedCard(card)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val amountDouble = amount.toDoubleOrNull()
                            if (amountDouble != null) {
                                viewModel.makePayment(receiverEmail, amountDouble, description)
                                onDismiss()
                            }
                        }
                    ) {
                        Text(stringResource(id = R.string.transfer2))
                    }
                }
            }
        }
    }
}


class QRCodeAnalyzer(private val onQrCodeScanned: (String) -> Unit) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { onQrCodeScanned(it) }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}

fun generateQRCode(content: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val width = bitMatrix.width
    val height = bitMatrix.height
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    for (x in 0 until width) {
        for (y in 0 until height) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}

@Composable
fun CameraPreview(
    onQrCodeScanned: (String) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    AndroidView(
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val preview = Preview.Builder().build()
            val selector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            preview.setSurfaceProvider(previewView.surfaceProvider)
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QRCodeAnalyzer { result ->
                    onQrCodeScanned(result)
                }
            )
            try {
                cameraProviderFuture.get().unbindAll()
                cameraProviderFuture.get().bindToLifecycle(
                    lifecycleOwner,
                    selector,
                    preview,
                    imageAnalysis
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            previewView
        },
        modifier = Modifier.fillMaxSize()
    )
}
