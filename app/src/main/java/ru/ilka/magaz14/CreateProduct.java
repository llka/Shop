package ru.ilka.magaz14;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class CreateProduct extends Activity implements View.OnClickListener {
    EditText etName, etPrice;
    TextView tvCheck;
    Button btnOk;

    String changed_name;
    int changed_price, changed_img = R.drawable.putin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_product);

        tvCheck = (TextView) findViewById(R.id.textView1);
        etName = (EditText) findViewById(R.id.etName);
        etPrice = (EditText) findViewById(R.id.etPrice);
        btnOk = (Button) findViewById(R.id.btnOk);

        btnOk.setOnClickListener(this);
        etName.setOnClickListener(this);
        etPrice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.etName:
                changed_name = etName.getText().toString();
                tvCheck.setText(changed_name);
                break;

            case R.id.etPrice:
                changed_price = Integer.parseInt(etPrice.getText().toString());
                tvCheck.setText(changed_price + "");
                break;

            case R.id.btnOk:
                Intent intent = new Intent();
                intent.putExtra("_name", changed_name);
                intent.putExtra("_price", changed_price);
                intent.putExtra("_img", changed_img);
                setResult(RESULT_OK, intent);
                finish();
                break;

        }
    }

}
