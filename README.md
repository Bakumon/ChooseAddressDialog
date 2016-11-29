# ChooseAddressDialog
选择省市区对话框 [Demo 下载](https://raw.githubusercontent.com/Bakumon/ChooseAddressDialog/master/apk/chooseAddressDemo.apk)

![](gif/GIF_20161125_225415.gif)


## style

利用 `WindowManager` 设置 Dialog 的布局和入场动画，解决 Dialog 位于底部宽度无法充满屏幕。

详细看这里：[自定义底部省市区选择 Dialog——宽度充满屏幕](http://bakumon.me/2016/11/24/android-dialog-bottom/)

## usage

```java
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
        //.setInitPCDName("陕西省", "西安市", "雁塔区")
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
```