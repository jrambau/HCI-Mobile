import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.example.lupay.MyApplication
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import viewmodels.QrViewModel
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
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
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    val swipeableState = rememberSwipeableState(initialValue = 1)
    val anchors = mapOf(0f to 1, 1f to 0)

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    var showTransferDialog by remember { mutableStateOf(false) }
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isAccount by remember { mutableStateOf(true) }

    val userEmail by viewModel.userEmail.collectAsState()
    val paymentResult by viewModel.paymentResult.collectAsState()

    LaunchedEffect(key1 = cameraProviderFuture, key2 = isCameraActive) {
        if (hasCameraPermission && isCameraActive) {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().apply {
                previewView?.surfaceProvider?.let { setSurfaceProvider(it) }
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QRCodeAnalyzer(context) { result ->
                    scannedResult = result
                    showTransferDialog = true
                    isCameraActive = false
                }
            )

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    LaunchedEffect(key1 = swipeableState.currentValue) {
        if (swipeableState.currentValue == 1 && isCameraActive) {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()

            val preview = Preview.Builder().build().apply {
                previewView?.surfaceProvider?.let { setSurfaceProvider(it) }
            }

            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                QRCodeAnalyzer(context) { result ->
                    scannedResult = result
                    showTransferDialog = true
                    isCameraActive = false
                }
            )

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = {
                showTransferDialog = false
                isCameraActive = true
            },
            title = { Text("Transfer") },
            text = {
                Column {
                    TextField(
                        value = scannedResult ?: "",
                        onValueChange = { /* Read-only */ },
                        label = { Text("Recipient's email") },
                        readOnly = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = amount,
                        onValueChange = { amount = it },
                        label = { Text("Amount") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Description") }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        FilterChip(
                            selected = isAccount,
                            onClick = { isAccount = true },
                            label = { Text("Account") }
                        )
                        FilterChip(
                            selected = !isAccount,
                            onClick = { isAccount = false },
                            label = { Text("Card") }
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amountDouble = amount.toDoubleOrNull()
                        if (amountDouble != null && scannedResult != null) {
                            viewModel.makePayment(scannedResult!!, amountDouble, description, isAccount, null) // Assuming no card selection for now
                            showTransferDialog = false
                            isCameraActive = true
                        }
                    },
                    enabled = amount.isNotEmpty() && amount.toDoubleOrNull() != null && scannedResult != null
                ) {
                    Text("Transfer")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showTransferDialog = false
                    isCameraActive = true
                }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "QR Scanner") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Go Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        content = { padding ->
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .swipeable(
                        state = swipeableState,
                        anchors = anchors,
                        orientation = Orientation.Vertical
                    )
            ) {
                if (swipeableState.currentValue == 1 && isCameraActive && hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewViewInstance = PreviewView(ctx)
                            previewView = previewViewInstance
                            previewViewInstance
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Swipe to QR Code",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .size(40.dp)
                    )
                }

                if (swipeableState.currentValue == 0) {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.background),
                        verticalArrangement = Arrangement.SpaceBetween,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(32.dp)
                                .background(MaterialTheme.colorScheme.background, MaterialTheme.shapes.medium)
                                .padding(16.dp)
                        ) {
                            userEmail?.let {
                                val bitmap = generateQRCode(it)
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null)
                            }
                        }

                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Swipe to Camera",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                                .size(40.dp)
                        )
                    }
                }
            }
        }
    )

    LaunchedEffect(viewModel.uiState) {
        val uiState = viewModel.uiState
        if (!uiState.isFetching) {
            if (uiState.successMessage != null) {
                // Show success message
                // You can use a Snackbar or another AlertDialog here
            } else if (uiState.error != null) {
                // Show error message
                // You can use a Snackbar or another AlertDialog here
            }
        }
    }
}

// QRCodeAnalyzer and generateQRCode functions remain unchanged
class QRCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    barcodes.firstOrNull()?.rawValue?.let { result ->
                        onQrCodeScanned(result)
                    }
                }
                .addOnFailureListener {
                    // Handle failure if needed
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
    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
    for (x in 0 until 512) {
        for (y in 0 until 512) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
        }
    }
    return bitmap
}
