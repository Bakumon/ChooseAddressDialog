package io.github.bakumon.chooseaddressdialog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.List;

import me.bakumon.chooseaddressdialog.ChooseAddressDialog;
import me.bakumon.chooseaddressdialog.DefaultAddressProvider;


public class MainActivity extends AppCompatActivity {

    private ChooseAddressDialog.Province mSelectedProvince;
    private ChooseAddressDialog.City mSelectedCity;
    private ChooseAddressDialog.Area mSelectedArea;

    private Button mBtnAffirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBtnAffirm = (Button) findViewById(R.id.btn_choose_address);

        // 解析数据需要放在非 UI 线程中
        final List<ChooseAddressDialog.Province> listProvinces = new DefaultAddressProvider().getAddressData(MainActivity.this);

        mBtnAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ChooseAddressDialog(MainActivity.this)
                        // 如果不设置，每次显示对话框都会加载一次默认的数据，这样不好
                        .setAddressData(listProvinces)
                        .setInitPCDCode(mSelectedProvince != null ? mSelectedProvince.id : "",
                                mSelectedCity != null ? mSelectedCity.id : "",
                                mSelectedArea != null ? mSelectedArea.id : "")
//                        .setInitPCDName("陕西省", "西安市", "雁塔区")
                        .setChoiceCompleteListeners(new ChooseAddressDialog.OnChoiceCompleteListeners() {
                            @Override
                            public void onChoiceComplete(ChooseAddressDialog.Province province, ChooseAddressDialog.City city, ChooseAddressDialog.Area areaFor) {
                                mSelectedProvince = province;
                                mSelectedCity = city;
                                mSelectedArea = areaFor;
                                if (mSelectedProvince != null && mSelectedCity != null && mSelectedArea != null) {
                                    mBtnAffirm.setText("" + mSelectedProvince.name + mSelectedCity.name + mSelectedArea.name);
                                }
                            }
                        })
                        .show();
            }
        });
    }
}
