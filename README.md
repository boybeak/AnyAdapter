# AnyAdapter [ ![Download](https://api.bintray.com/packages/boybeak/nulldreams/any-adapter/images/download.svg) ](https://bintray.com/boybeak/nulldreams/any-adapter/_latestVersion)

## Install

```groovy
buildscript {
  repositories {
        jcenter()
    }
}
```

```groovy
implementation 'com.github.boybeak:any-adapter:version'
```



## Usage

```kotlin
val adapter = new AnyAdapter()
// Or you can use FooterAdapter, AutoFooterAdapter
recyclerView.adapter = adapter
```

All your data model should be wrapped by `ItemImpl<T>`. The type `T` is your data type. AnyAdapter only accepts `ItemImpl`'s subclass as it's data item. Actually, you just need to make your data item class extends `AbsItem`.

```kotlin
// A data example.
data class Post(val id: Int, val title: String, val text: String)
```

```kotlin
// An item example.
class PostItem(post: Post) : AbsItem<Post>(post) {
	override fun layoutId(): Int {
        return R.layout.xxx
    }

    override fun holderClass(): Class<PostHolder> {
        return PostHolder::class.java
    }

    override fun areContentsSame(other: ItemImpl<*>): Boolean {
        if (other is PostItem) {
            val os = other.source()
            return source().id == os.id && source().title == os.title 
          		&& source().text == os.text
        }
        return false
    }
}
```

```kotlin
// A holder example
class PostHolder(v: View) : AbsHolder<PostItem>(v) {
  override fun onBind(item: JourneyItem, position: Int, absAdapter: AnyAdapter) {
    // Fill content to the view.
  }
}
```



### add and addAll

```kotlin
val post = xxx
adapter.add(PostItem(post))
// Or adapter.add(position, PostItem(post))
```

This is a typical `add` operation. No need to do any **notifyDataXXXChanged** operations. The data will refresh automatically.

```kotlin
val posts = ArrayList<Post>();
val postItems = List(posts.size) { index ->
  PostItem(posts[index])
}
// Fill data to post
adapter.addAll(postItems)
// Or adapter.add(position, postItems)
```

Actually, there's another better function.

```kotlin
adapter.addAll(posts, object : IEachConverter<Post, PostItem> { s, position ->
  PostItem(s)
})
/* With lamada
* adapter.addAll(posts) { s, position ->
	PostItem(s)
}
*/
```

If 

