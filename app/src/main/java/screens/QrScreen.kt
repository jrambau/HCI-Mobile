package com.example.lupay.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

@Composable
fun QRScreen() {
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
    var apiResult by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()

    val savedData = remember { mutableStateOf<String?>(null) }

    // Retrieve the last scanned QR code
    LaunchedEffect(key1 = true) {
        savedData.value = getScannedData(context)
    }

    // Request camera permission
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Placeholder for the top bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Assuming the height of the top bar
            contentAlignment = Alignment.Center
        ) {
            Text("Top Bar")
        }

        if (hasCameraPermission) {
            // Camera Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f), // Occupy all available space
            ) {
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
                            QRCodeAnalyzer(context) { result ->
                                scannedResult = result
                                coroutineScope.launch {
                                    apiResult = fetchDataFromApi(result)
                                }
                            }
                        )
                        try {
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
        } else {
            Text(
                "Camera permission is required to scan QR codes",
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight(align = Alignment.CenterVertically)
            )
        }

        // Placeholder for the bottom bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp), // Assuming the height of the bottom bar
            contentAlignment = Alignment.Center
        ) {
            Text("Bottom Bar")
        }
    }
}



class QRCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { result ->
                            saveScannedData(context, result) // Save the result locally
                            onQrCodeScanned(result)
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle any errors
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}

suspend fun fetchDataFromApi(qrContent: String): String {
    return withContext(Dispatchers.IO) {
        kotlinx.coroutines.delay(1000) // Simulating API call delay
        "Info for QR content: $qrContent"
    }
}

fun saveScannedData(context: Context, data: String) {
    val sharedPreferences = context.getSharedPreferences("QRData", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("last_scanned_qr", data)
    editor.apply()
}

fun getScannedData(context: Context): String? {
    val sharedPreferences = context.getSharedPreferences("QRData", Context.MODE_PRIVATE)
    return sharedPreferences.getString("last_scanned_qr", null)
}
