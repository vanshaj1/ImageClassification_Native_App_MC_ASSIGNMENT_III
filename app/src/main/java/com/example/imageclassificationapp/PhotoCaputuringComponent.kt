package com.example.imageclassificationapp

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.imageclassificationapp.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer
import java.nio.ByteOrder


// citation:- https://www.youtube.com/watch?v=yV9nrRIC_R0 , https://www.youtube.com/watch?v=So1Bs8CmSa8&t=11s , https://medium.com/@khaingsuthway_72707/unlocking-the-power-of-lightweight-neural-networks-on-mobile-devices-685882ebb51f , https://github.com/Gerrix90/JetpackComposePlayground/tree/main

lateinit var model: Model
external fun generateByteBuffer(byteBuffer: ByteBuffer, rgbValuesArray: IntArray ,imageSize: Int)
@Composable
fun PhotoCapturingComponent() {

    val context = LocalContext.current

    // initializing ML classifier instance
    model = Model.newInstance(context)


    var classificationResult = remember {
        mutableStateOf("")
    }

    var imageBitmap by remember {
        mutableStateOf<Bitmap?>(null)
    }

    var imagePath by remember {
        mutableStateOf<Uri?>(null)
    }

    val FilePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = {
            imagePath = it
        }
    )

    Column(
        Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        imagePath?.let {
            if (Build.VERSION.SDK_INT < 28)
                imageBitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            else {
                val source = ImageDecoder.createSource(context.contentResolver, it)
                imageBitmap = ImageDecoder.decodeBitmap(
                    source,
                    ImageDecoder.OnHeaderDecodedListener { decoder, info, source ->
                        decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE
                        decoder.isMutableRequired = true
                    })
            }
        }

        imageBitmap?.let {
            val scaledBitmap = Bitmap.createScaledBitmap(it, imageSize, imageSize, false);

            Image(
                 bitmap = it.asImageBitmap(),
                null,
                 modifier = Modifier.size(400.dp)
            )
            Spacer(modifier = Modifier.padding(20.dp))

            Text(text = "This image is of:- ${classificationResult.value}")

            Spacer(modifier = Modifier.padding(20.dp))

            Button(onClick = {
                classificationResult.value = classification(scaledBitmap)
            }) {
                Text("Ask the machine")
            }
        }

        Spacer(modifier = Modifier.padding(20.dp))

        Button(onClick = {
            classificationResult.value = ""
            FilePicker.launch("image/*")
        }) {
            Text(text = "Select an Image")
        }
    }
}

val imageSize = 32


fun classification(image: Bitmap):String {

    // Input creating from image bitmap for model.
    val imageFeatures = TensorBuffer.createFixedSize(
                            intArrayOf(1, 32, 32, 3),
                            DataType.FLOAT32
                        )

    val byteBuffer: ByteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)

    byteBuffer.order(ByteOrder.nativeOrder())

    val rgbValuesArray = IntArray(imageSize * imageSize)

    image.getPixels(rgbValuesArray, 0, image.width, 0, 0, image.width, image.height)


    // Native function called to fill up the byteBuffer data
    generateByteBuffer(byteBuffer, rgbValuesArray,imageSize)



    // image features generation from byte buffer
    imageFeatures.loadBuffer(byteBuffer)



    // Model inference
    val output: Model.Outputs = model.process(imageFeatures)
    val outputFeatures: TensorBuffer = output.getOutputFeature0AsTensorBuffer()
    val confidences = outputFeatures.floatArray


    // Finding the target variable that is having more probability
    var mostProbable = 0
    var maxConfidence = 0f
    var idx = 0
    while (idx < confidences.size) {
        val value = confidences[idx]
        if (value > maxConfidence) {
            maxConfidence = value
            mostProbable = idx
        }
        idx++
    }


    val targetVariables = arrayOf("Apple", "Banana", "Orange")
    Log.d("Testing",targetVariables[mostProbable])


    // closing the model or free the resources occupied by model
    model.close()
    return targetVariables[mostProbable]
}