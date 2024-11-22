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
import androidx.navigation.NavController
import androidx.wear.compose.material.ExperimentalWearMaterialApi
import androidx.wear.compose.material.rememberSwipeableState
import androidx.wear.compose.material.swipeable
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.util.concurrent.Executors

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWearMaterialApi::class)
@Composable
fun QRScreen(navController: NavController) {
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

    // Swipeable state setup
    val swipeableState = rememberSwipeableState(initialValue = 1) // Start with the QR code view
    val anchors = mapOf(0f to 1, 1f to 0) // Swap the anchors

    // Camera permission launcher
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
    }

    // Show dialog when QR code is scanned
    var showDialog by remember { mutableStateOf(false) }

    // Camera initialization with analyzer
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
                    showDialog = true // Show dialog when QR code is found
                    isCameraActive = false // Stop the camera
                }
            )

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    // Reinitialize camera when swiping back to camera
    LaunchedEffect(key1 = swipeableState.currentValue) {
        if (swipeableState.currentValue == 1 && isCameraActive) {
            // Reinitialize the camera when swiping back to the camera view
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
                    showDialog = true // Show dialog when QR code is found
                    isCameraActive = false // Stop the camera
                }
            )

            cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageAnalysis)
        }
    }

    // Dialog to show QR code result
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("QR Code Scanned") },
            text = { Text("Scanned QR Code: $scannedResult") },
            confirmButton = {
                TextButton(onClick = {
                    showDialog = false
                    // Ensure smooth transition back to the QR Code view
                    isCameraActive = true // Reactivate the camera if needed
                }) {
                    Text("Confirm")
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
                // Camera view (State 0)
                if (swipeableState.currentValue == 1 && isCameraActive && hasCameraPermission) {
                    AndroidView(
                        factory = { ctx ->
                            val previewViewInstance = PreviewView(ctx)
                            previewView = previewViewInstance
                            previewViewInstance
                        },
                        modifier = Modifier.fillMaxSize()
                    )

                    // White arrow at the bottom (for swipe indication)
                    Icon(
                        Icons.Default.ArrowUpward,
                        contentDescription = "Swipe to QR Code",
                        tint = Color.White,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .size(40.dp) // Adjust size to match the QR code arrow
                    )
                }

                // QR Code view (State 1)
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
                            scannedResult?.let {
                                val bitmap = generateQRCode(it)
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null)
                            } ?: run {
                                val placeholderData = "No QR Code Scanned"
                                val bitmap = generateQRCode(placeholderData)
                                Image(bitmap = bitmap.asImageBitmap(), contentDescription = null)
                            }
                        }

                        // Icon to swipe back to camera
                        Icon(
                            Icons.Default.ArrowDownward,
                            contentDescription = "Swipe to Camera",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Black,
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(16.dp)
                                .size(40.dp) // Adjust size to match the camera arrow
                        )
                    }
                }
            }
        }
    )
}

// QR Code analyzer class remains unchanged
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

// Function to generate QR code bitmap
fun generateQRCode(content: String): Bitmap {
    val writer = QRCodeWriter()
    val bitMatrix = writer.encode(content, BarcodeFormat.QR_CODE, 512, 512)
    val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
    for (x in 0 until 512) {
        for (y in 0 until 512) {
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)        }
    }
    return bitmap
}
