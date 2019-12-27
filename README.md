# PermissionKit
Android 6.0 最简单的权限工具类,基于Androidx,非注解方式
### 示例图
|正常申请权限|拒绝后再申请|拒绝后申请出现询问checkbox|
|:---:|:---:|:---:|
| ![正常申请权限](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E6%AD%A3%E5%B8%B8%E8%AF%B7%E6%B1%82.png "正常申请权限")|![拒绝后再申请](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E6%8B%92%E7%BB%9D%E5%90%8E%E5%86%8D%E7%94%B3%E8%AF%B7.png "拒绝后再申请")|![拒绝后申请出现询问checkbox](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E6%8B%92%E7%BB%9D%E5%90%8E%E7%94%B3%E8%AF%B7%E5%87%BA%E7%8E%B0%E8%AF%A2%E9%97%AEcheckbox.png "拒绝后申请出现询问checkbox")|

|点击不再询问并拒绝|拒绝并不再询问后提示设置|前往设置界面|
|:---:|:---:|:---:|
|![点击不再询问并拒绝](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E7%82%B9%E5%87%BB%E4%B8%8D%E5%86%8D%E8%AF%A2%E9%97%AE%E5%B9%B6%E6%8B%92%E7%BB%9D.png "点击不再询问并拒绝")|![拒绝并不再询问后提示设置](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E6%8B%92%E7%BB%9D%E5%B9%B6%E4%B8%8D%E5%86%8D%E8%AF%A2%E9%97%AE%E5%90%8E%E6%8F%90%E7%A4%BA%E8%AE%BE%E7%BD%AE.png "拒绝并不再询问后提示设置")|![前往设置界面](https://raw.githubusercontent.com/ProgressiveDevelop/PermissionKit/master/screenshot/%E5%89%8D%E5%BE%80%E8%AE%BE%E7%BD%AE%E7%95%8C%E9%9D%A2.png "前往设置界面")|
### 如何使用
#### 第一步，在 AndroidManifest.xml 中添加权限
```
    //比如
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
```
#### 第二步，需要请求权限的 Activity 继承 PermissionActivity或者自行实现 OnRequestPermissionsResultCallback 接口
```
    //自行实现实例
     /**
     * 请求权限结果
     *
     * @param requestCode  请求码
     * @param permissions  权限数组
     * @param grantResults 结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> unAgreeList = new ArrayList<>();
        for (int i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                unAgreeList.add(permissions[i]);
            }
        }
        PermissionKit.getInstance().requestPermissionsResult(requestCode, permissions, unAgreeList);
    }
```
### 第三步，请求运行所需权限
```
                //请求权限
                PermissionKit.getInstance().requestPermission(MainActivity.this, 200, new OnPermissionListener() {
                    @Override
                    public void onSuccess(int requestCode, String... permissions) {
                        Log.e(getClass().getSimpleName(), "onSuccess :" + Arrays.toString(permissions));
                    }

                    @Override
                    public void onFail(int requestCode, String... permissions) {
                        Log.e(getClass().getSimpleName(), "onFail " + Arrays.toString(permissions));
                        //授权失败后再次操作
                        PermissionKit.getInstance().guideSetting(MainActivity.this, true, requestCode, null, permissions);
                    }
                }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE);
```
### 注意
在用户拒绝授权后，开发者可在回调接口 onFail 中自行处理，也可使用已有机制再次引导用户设置,具体方法参考：guideSetting();
### 主要接口
```
    /**
     * 获取权限工具类
     *
     * @return 权限工具对象
     */
    public static PermissionKit getInstance()
    
     /**
     * 是否是6.0及以上版本
     *
     * @return true|false
     */
    public boolean isM() {
    
     /**
     * 检查应用是否有该权限
     *
     * @param context     当前 context
     * @param permissions 权限数组{Manifest.permission.CAMERA}
     * @return true ==> 已经授权
     */
    public boolean hasPermission(Context context, String... permissions) 
    
     /**
     * 申请权限
     *
     * @param activity    当前Activity
     * @param requestCode 请求码
     * @param callback    回调接口
     * @param permissions 权限
     */
    public void requestPermission(Activity activity, int requestCode, OnPermissionListener callback, String... permissions) 
    
    /**
     * 拒绝后引导去设置
     *
     * @param activity     当前Activity
     * @param defaultGuide 是否使用默认引导
     * @param requestCode  请求码
     * @param permissions  未授权权限
     * @param customGuide  用户自定义引导设置权限接口：defaultGuide 为true时，不应 null
     */
    public void guideSetting(Activity activity, boolean defaultGuide, int requestCode, onCustomGuide customGuide, String... permissions) 
    
    /**
     * 申请权限结果   :  如果不继承 PermissionActivity，开发者需要自行回调处理，代码可参考 PermissionActivity
     *
     * @param requestCode 请求码
     * @param permissions 所有请求权限
     * @param unAgreeList 未同意的权限
     */
   public void requestPermissionsResult(int requestCode, String[] permissions, List<String> unAgreeList) {
```
### License
```
Copyright (C)  ProgressiveDevelop(https://github.com/ProgressiveDevelop/PermissionKit)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```