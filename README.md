# BaseUtils
[![](https://jitpack.io/v/MrNinso/BaseUtils.svg)](https://jitpack.io/#MrNinso/BaseUtils)
![Gradle bintray Package](https://github.com/MrNinso/BaseUtils/workflows/Gradle%20bintray%20Package/badge.svg)


BaseUtils is a toolbox to help you interact with the android system and codeless.

## How to use Base Utils?

 1. Add it to your root build.gradle at the end of repositories

````
allprojects {
	repositories {
		...
		maven { url 'https://dl.bintray.com/developer3/com.developer3.libs' }
	}
}
````


 2. Add the dependency
 ````css
dependencies {
		implementation 'com.developer3:BaseUtils:1.0.8.RC'
}
````

3.  (Optional) Add this line to your main activity or in your application class if you need to use some android features

````
BaseDevice.getApp(this.getApplication());
````
