package com.example.facedetection;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.facedetection.Helper.GraphicOverlay;
import com.example.facedetection.Helper.RectOverlay;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.shashank.sony.fancydialoglib.Animation;
import com.shashank.sony.fancydialoglib.FancyAlertDialog;
import com.wonderkiln.camerakit.CameraKitError;
import com.wonderkiln.camerakit.CameraKitEvent;
import com.wonderkiln.camerakit.CameraKitEventListener;
import com.wonderkiln.camerakit.CameraKitImage;
import com.wonderkiln.camerakit.CameraKitVideo;
import com.wonderkiln.camerakit.CameraView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {
    private CircleImageView faceDetectButton;
    private GraphicOverlay graphicOverlay;
    private CameraView cameraView;
    AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();


        faceDetectButton=findViewById(R.id.detect_face_btn);
        graphicOverlay=findViewById(R.id.graphic_overlay);
        cameraView=findViewById(R.id.camera_view);


        alertDialog=new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Please Wait, Loading...")
                .setCancelable(false)
                .build();

        faceDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraView.start();
                cameraView.captureImage();
                graphicOverlay.clear();
//                faceDetectButton.setText("Detect Face");
            }
        });
        cameraView.addCameraKitListener(new CameraKitEventListener() {
            @Override
            public void onEvent(CameraKitEvent cameraKitEvent) {

            }

            @Override
            public void onError(CameraKitError cameraKitError) {

            }

            @Override
            public void onImage(CameraKitImage cameraKitImage) {
                alertDialog.show();
                Bitmap bitmap=cameraKitImage.getBitmap();
                bitmap=Bitmap.createScaledBitmap(bitmap,cameraView.getWidth(),cameraView.getHeight(),false);
                cameraView.stop();

                processFaceReduction(bitmap);
            }
            @Override
            public void onVideo(CameraKitVideo cameraKitVideo) {

            }
        });


    }

    private void processFaceReduction(Bitmap bitmap) {
        FirebaseVisionImage firebaseVisionImage=FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionFaceDetectorOptions firebaseVisionFaceDetectorOptions=new FirebaseVisionFaceDetectorOptions.Builder().build();
        FirebaseVisionFaceDetector firebaseVisionFaceDetector= FirebaseVision.getInstance()
                .getVisionFaceDetector(firebaseVisionFaceDetectorOptions);
        firebaseVisionFaceDetector.detectInImage(firebaseVisionImage)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                             getFaceResults(firebaseVisionFaces);

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,"Error"+e.getMessage(),Toast.LENGTH_SHORT);
            }
        });

        }

    private void getFaceResults(List<FirebaseVisionFace> firebaseVisionFaces) {
        int counter=0;
        for(FirebaseVisionFace face:firebaseVisionFaces){
            Rect rect=face.getBoundingBox();
            RectOverlay rectOverlay=new RectOverlay(graphicOverlay,rect);
            graphicOverlay.add(rectOverlay);
            counter=counter+1;
        }
        alertDialog.dismiss();
        if(counter==0){
           FancyAlertDialog.Builder
                    .with(this)
                    .setTitle("No face detected")
                    .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                    .setMessage("Click below two scan once again")
                    .setNegativeBtnText("Watch")
                    .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                    .setPositiveBtnText("Scan Again")
                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_person_white, View.VISIBLE)
                    .onPositiveClicked(dialog -> onResume())
                    .onNegativeClicked(dialog -> onPause())
                    .build()
                    .show();

        }
        else if(counter==1){
            FancyAlertDialog.Builder
                    .with(this)
                    .setTitle("1 face detected")
                    .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                    .setMessage("Click below two scan once again")
                    .setNegativeBtnText("Watch")
                    .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                    .setPositiveBtnText("Scan Again")
                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_person_white, View.VISIBLE)
                    .onPositiveClicked(dialog -> resume())
                    .onNegativeClicked(dialog -> onPause())
                    .build()
                    .show();
        }
        else{
            FancyAlertDialog.Builder
                    .with(this)
                    .setTitle(+counter+" faces detected")
                    .setBackgroundColor(Color.parseColor("#303F9F"))  // for @ColorRes use setBackgroundColorRes(R.color.colorvalue)
                    .setMessage("Click below two scan once again")
                    .setNegativeBtnText("Watch")
                    .setPositiveBtnBackground(Color.parseColor("#FF4081"))  // for @ColorRes use setPositiveBtnBackgroundRes(R.color.colorvalue)
                    .setPositiveBtnText("Scan Again")
                    .setNegativeBtnBackground(Color.parseColor("#FFA9A7A8"))  // for @ColorRes use setNegativeBtnBackgroundRes(R.color.colorvalue)
                    .setAnimation(Animation.POP)
                    .isCancellable(true)
                    .setIcon(R.drawable.ic_person_white, View.VISIBLE)
                    .onPositiveClicked(dialog -> resume())
                    .onNegativeClicked(dialog -> onPause())
                    .build()
                    .show();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        cameraView.stop();
//        faceDetectButton.setText("Scan Again");
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraView.start();
//        faceDetectButton.setText("Detect Face");
    }

    public void resume(){
        super.onResume();
        cameraView.start();
        graphicOverlay.clear();
    }


}