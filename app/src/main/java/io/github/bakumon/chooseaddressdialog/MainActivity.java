package io.github.bakumon.chooseaddressdialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import io.github.bakumon.chooseaddressdialog.view.ChooseAddressDialog;

public class MainActivity extends AppCompatActivity {

    private ChooseAddressDialog.Province mSelectedProvince;
    private ChooseAddressDialog.City mSelectedCity;
    private ChooseAddressDialog.District mSelectedDistrict;

    private Button mBtnAffirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnAffirm = (Button) findViewById(R.id.btn_choose_address);
        mBtnAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChooseAddressDialog mChoiceManageAddressDialog;
                if (mSelectedProvince != null && mSelectedCity != null && mSelectedDistrict != null) {
                    // 这里传 id 或 name 都可以
                    mChoiceManageAddressDialog = new ChooseAddressDialog(MainActivity.this, mSelectedProvince.id, mSelectedCity.id, mSelectedDistrict.id);
                } else {
                    mChoiceManageAddressDialog = new ChooseAddressDialog(MainActivity.this);
                }
                mChoiceManageAddressDialog.setChoiceCompleteListeners(new ChooseAddressDialog.OnChoiceCompleteListeners() {
                    @Override
                    public void onChoiceComplete(ChooseAddressDialog.Province province, ChooseAddressDialog.City city, ChooseAddressDialog.District districtFor) {
                        mSelectedProvince = province;
                        mSelectedCity = city;
                        mSelectedDistrict = districtFor;
                        if (mSelectedProvince != null && mSelectedCity != null && mSelectedDistrict != null) {
                            mBtnAffirm.setText("" + mSelectedProvince.name + mSelectedCity.name + mSelectedDistrict.name);
                        }
                    }
                });
                mChoiceManageAddressDialog.show();
            }
        });
    }
}
