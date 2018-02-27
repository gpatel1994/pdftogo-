package com.example.patel.pdftogo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import canvas.PaintView;

public class CanvasActivity extends AppCompatActivity {

    private PaintView paintView;
    private Toolbar toolbar;
    public static final int REQUEST_PERMISSIONS = 1;
    String  pdfFileName,mEditText,targetPdf;
    File filePath;
    EditText editText;
    Boolean textActionClicked = false, gallrayActionClicked = false;
    Bitmap mbitmap;
    ImageView imageView;
    Image img = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.canvas_layout);
        pdfFileName = getIntent().getStringExtra("pdfFileName");
        paintView = (PaintView) findViewById(R.id.paintView);
        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);


        // to inflate the blank canvas
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics displaymetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        paintView.init(displaymetrics);

        //setting up the toolbar for activity
        toolbar = (Toolbar) findViewById(R.id.canvas_toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {

            //getting the Name to set on toolbar
            getSupportActionBar().setTitle(pdfFileName);
        }
        toolbar.inflateMenu(R.menu.main);
        fn_permission();

        //setting path for PDF File
        targetPdf = "/sdcard/"+pdfFileName+".pdf";
        filePath = new File(targetPdf);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.texticon:
                paintView.setVisibility(View.GONE);
                textActionClicked = true;
                return true;
            case R.id.picture:
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 0);
                paintView.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                gallrayActionClicked = true;
            case R.id.normal:
                paintView.normal();
                return true;
            case R.id.emboss:
                paintView.emboss();
                return true;
            case R.id.blur:
                paintView.blur();
                return true;
            case R.id.clear:
                paintView.clear();
                return true;
            case R.id.save:
                if(textActionClicked){
                    //method for generating text into pdf
                    createTextPdf();
                }
                else if (gallrayActionClicked){   //method for generating canvas bitmap to pdf
                    createImagetoPdf();
                }
                else{
                    createCanvasPdf();
                }
                Toast.makeText(this, "Saving PDF", Toast.LENGTH_LONG).show();
            case R.id.cancle:
                Intent changeActivity = new Intent(this, MainActivity.class);
                startActivity(changeActivity);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void createImagetoPdf() {
        try {
            Document document = new Document(PageSize.A4, 38, 38, 50, 38);
            Rectangle documentRect = document.getPageSize();

            filePath = new File(targetPdf);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mbitmap.compress(Bitmap.CompressFormat.PNG,25, stream);
            byte[] byteArray = stream.toByteArray();
            img = Image.getInstance(byteArray);
            if (mbitmap.getWidth() > documentRect.getWidth()
                    || mbitmap.getHeight() > documentRect.getHeight()) {
                //bitmap is larger than page,so set bitmap's size similar to the whole page
                img.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
            } else {
                //bitmap is smaller than page, so add bitmap simply.
                //[note: if you want to fill page by stretching image,
                // you may set size similar to page as above]
                img.scaleAbsolute(mbitmap.getWidth(), mbitmap.getHeight());
            }


            img.setAbsolutePosition(
                    (documentRect.getWidth() - img.getScaledWidth()) / 2,
                    (documentRect.getHeight() - img.getScaledHeight()) / 2);
            Log.v("Stage 7", "Image Alignments");

            img.setBorder(Image.BOX);

            img.setBorderWidth(15);

            document.add(img);
            document.newPage();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Uri targetUri = data.getData();
            try {
                mbitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(targetUri));
               // paintView.getmCanvas().drawBitmap(mbitmap,0,0,null);
                imageView.setImageBitmap(mbitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void createTextPdf(){
        Document textDocument = new Document();
        try {
            mEditText = editText.getText().toString();
            filePath = new File(targetPdf);
            PdfWriter.getInstance(textDocument,new FileOutputStream(filePath));
            textDocument.open();
            textDocument.add(new Paragraph(mEditText));
            textDocument.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
    private void createCanvasPdf() {
        FrameLayout root = findViewById(R.id.iframelayout);
        root.setDrawingCacheEnabled(true);
        root.buildDrawingCache();
        mbitmap= root.getDrawingCache();
        try {
            Document document = new Document(PageSize.A4, 38, 38, 50, 38);
            Rectangle documentRect = document.getPageSize();
            filePath = new File(targetPdf);
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mbitmap.compress(Bitmap.CompressFormat.PNG,25, stream);
            byte[] byteArray = stream.toByteArray();
            img = Image.getInstance(byteArray);
            if (mbitmap.getWidth() > documentRect.getWidth()
                    || mbitmap.getHeight() > documentRect.getHeight()) {
                //bitmap is larger than page,so set bitmap's size similar to the whole page
                img.scaleAbsolute(documentRect.getWidth(), documentRect.getHeight());
            } else {
                //bitmap is smaller than page, so add bitmap simply.
                //[note: if you want to fill page by stretching image,
                // you may set size similar to page as above]
                img.scaleAbsolute(mbitmap.getWidth(), mbitmap.getHeight());
            }
            img.setAbsolutePosition(
                    (documentRect.getWidth() - img.getScaledWidth()) / 2,
                    (documentRect.getHeight() - img.getScaledHeight()) / 2);
            img.setBorder(Image.BOX);
            img.setBorderWidth(15);
            document.add(img);
            document.newPage();
            document.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fn_permission() {
        if ((ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)||
                (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            if ((ActivityCompat.shouldShowRequestPermissionRationale(CanvasActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE))) {


            } else {
                ActivityCompat.requestPermissions(CanvasActivity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
            if ((ActivityCompat.shouldShowRequestPermissionRationale(CanvasActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE))) {
                ActivityCompat.requestPermissions(CanvasActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
            } else {
                ActivityCompat.requestPermissions(CanvasActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);

            }
        } /*else {
            boolean_permission = true;


        }*/
    }

  /**************************************************************************************************************/

   /* @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                boolean_permission = true;


            } else {
                Toast.makeText(getApplicationContext(), "Please allow the permission", Toast.LENGTH_LONG).show();

            }
        }
    }*/
  /**************************************************************************************************************/

}

