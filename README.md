# ChooseAddressDialog
选择省市区对话框 [Demo 下载](https://raw.githubusercontent.com/Bakumon/ChooseAddressDialog/master/apk/chooseAddressDemo.apk)

![](gif/GIF_20161125_225415.gif)


## style

利用 `WindowManager` 设置 Dialog 的布局和入场动画，来解决 Dialog 位于底部宽度无法充满屏幕。

详细见：[自定义底部省市区选择 Dialog——宽度充满屏幕](http://bakumon.me/2016/11/24/android-dialog-bottom/)

## 初始化

提供了三个构造器供初始化省市区使用，直接调用 show()显示

### 1.默认

默认弹出对话框显示 json 数据的第一个值，即　北京　北京　东城区

```java
public ChooseAddressDialog(Context context) {}
```
### 2.使用 code 码

默认弹出对话框显示 json 数据的第一个值，即　北京　北京　东城区

```java
public ChooseAddressDialog(Context context,
                            String initProvinceCode,
                            String initCityCode,
                            String initDistrictCode) {}
```
### 3.使用 name 值

默认弹出对话框显示 json 数据的第一个值，即　北京　北京　东城区

```java
    /**
     * 使用 code 或 name 初始化
     * note：省市区 code 或 name 需要一致
     *
     * @param context      上下文
     * @param initProvince 省 code 或 name
     * @param initCity     市 code 或 name
     * @param initDistrict 区 code 或 name
     * @param isName       是否是 name
     */
public ChooseAddressDialog(Context context,
                            String initProvince,
                            String initCity,
                            String initDistrict,
                            boolean isName) {}
```

## 监听器

```java
mChoiceManageAddressDialog.setChoiceCompleteListeners(new ChooseAddressDialog.OnChoiceCompleteListeners() {
     @Override
     public void onChoiceComplete(ChooseAddressDialog.Province province, ChooseAddressDialog.City city, ChooseAddressDialog.District districtFor) {
        // 设置选中省市区显示
        mTvArea.setText("" + mSelectedProvince.name + mSelectedCity.name + mSelectedDistrict.name);
     }
});
```

## 源数据

一套比较全的全国地区数据，如果因业务需要更换，需要重新实现 `void initLocate()` 方法读取省市区数据。

```json
{
	"p": [{
			"id": 3,
			"name": "北京市"
		},{
			"id": 4,
			"name": "天津市"
		},
		...
	"c": {
    		"3": [{
    			"id": 4,
    			"name": "北京市"
    		}],
    		"4": [{
    			"id": 5,
    			"name": "天津市"
    		}],
    	...
    "a": {
    		"4": [{
    			"id": 3,
    			"name": "东城区"
    		},
    		{
    			"id": 4,
    			"name": "西城区"
    		}
    	...
```
