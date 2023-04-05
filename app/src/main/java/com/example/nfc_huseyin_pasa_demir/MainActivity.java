package com.example.nfc_huseyin_pasa_demir;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.nfc_huseyin_pasa_demir.service.AppExecutors;
import com.example.nfc_huseyin_pasa_demir.service.Card;
import com.example.nfc_huseyin_pasa_demir.service.CardDatabase;

import company.tap.nfcreader.open.reader.TapEmvCard;
import company.tap.nfcreader.open.reader.TapNfcCardReader;
import company.tap.nfcreader.open.utils.TapCardUtils;
import company.tap.nfcreader.open.utils.TapNfcUtils;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;


public class MainActivity extends AppCompatActivity {
    private TapNfcCardReader tapNfcCardReader;
    private Disposable cardReadDisposable = Disposables.empty();
    private LinearLayout cardReadContent;
    private TextView scanCardContent, cardNumberText, expireDateText,cardType,noNfcText;
    private ProgressDialog mProgressDialog;
    private CardDatabase cardDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setId();
        tapNfcCardReader = new TapNfcCardReader(this);
        cardDB = CardDatabase.getInstance(getApplicationContext());
        createProgressDialog();
    }

    private void setId(){
        noNfcText = findViewById(android.R.id.candidatesArea);
        scanCardContent = findViewById(R.id.content_putCard);
        cardReadContent = findViewById(R.id.content_cardReady);
        cardNumberText = findViewById(R.id.card_number);
        expireDateText = findViewById(R.id.expire_date);
        cardType = findViewById(R.id.card_type);
    }

    @Override
    protected void onResume() {
        if (TapNfcUtils.isNfcAvailable(this)) {
            if (TapNfcUtils.isNfcEnabled(this)) {
                tapNfcCardReader.enableDispatch();
                scanCardContent.setVisibility(View.VISIBLE);
            } else
                enableNFC();
        } else {
            scanCardContent.setVisibility(View.GONE);
            cardReadContent.setVisibility(View.GONE);
            noNfcText.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mProgressDialog.show();
        if (tapNfcCardReader.isSuitableIntent(intent)) {
            mProgressDialog.show();
            cardReadDisposable = tapNfcCardReader
                    .readCardRx2(intent)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            this::showCardInfo,
                            throwable -> displayError(throwable.getMessage()));
        }
    }

    @Override
    protected void onPause() {
        cardReadDisposable.dispose();
        tapNfcCardReader.disableDispatch();
        super.onPause();
    }

    private void enableNFC() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.msg_info));
        alertDialog.setMessage(getString(R.string.enable_nfc));
        alertDialog.setPositiveButton(getString(R.string.msg_ok), (dialog, which) -> {
            noNfcText.setVisibility(View.GONE);
            dialog.dismiss();
            startActivity(new Intent(Settings.ACTION_NFC_SETTINGS));
        });
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (cardReadContent.isShown()) {
            scanCardContent.setVisibility(View.VISIBLE);
            cardReadContent.setVisibility(View.GONE);
        } else super.onBackPressed();
    }

    private void showCardInfo(TapEmvCard emvCard) {
        scanCardContent.setVisibility(View.GONE);
        cardReadContent.setVisibility(View.VISIBLE);
        cardNumberText.setText(TapCardUtils.formatCardNumber(emvCard.getCardNumber(), emvCard.getType()));
        expireDateText.setText(DateFormat.format("MM/yy", emvCard.getExpireDate()));
        cardType.setText(emvCard.getApplicationLabel());
        mProgressDialog.dismiss();
        saveCardData(emvCard);
    }

    private void saveCardData(TapEmvCard emvCard) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                boolean isNull = cardDB.nfcDao().findCard(emvCard.getCardNumber()) == null;
                if (isNull) {
                    cardDB.nfcDao().insert(new Card(emvCard.getCardNumber(), emvCard.getExpireDate().toString(), emvCard.getApplicationLabel()));
                    showToast(true);
                } else {
                    showToast(false);
                }

            }
        });
    }

    private void showToast(boolean isNull) {
        AppExecutors.getInstance().mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (isNull) {
                    Toast.makeText(getApplicationContext(),getString(R.string.add_card) , Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.read_card), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void displayError(String message) {
        noNfcText.setText(message);
    }
    private void createProgressDialog() {
        String title = getString(R.string.progressBar_title);
        String mess = getString(R.string.progressBar_mess);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle(title);
        mProgressDialog.setMessage(mess);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }
}
