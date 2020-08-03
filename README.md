# AnyAdapter [ ![Download](https://api.bintray.com/packages/boybeak/nulldreams/any-adapter/images/download.svg) ](https://bintray.com/boybeak/nulldreams/any-adapter/_latestVersion)

[中文README](https://github.com/boybeak/AnyAdapter/blob/master/README-CN.md)

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

<img src="https://github.com/boybeak/AnyAdapter/blob/master/gif/list.png" width="360" height="640"/>

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



### Add and addAll

#### add

```kotlin
val post = xxx
adapter.add(PostItem(post))
// Or adapter.add(position, PostItem(post))
```

This is a typical `add` operation. No need to do any **notifyDataXXXChanged** operations. The data will refresh automatically.

Another **one-to-many** add function that can convert one data element to item list.

```kotlin
adapter.add(post, object : IConverter<Post, TextItem> {
  override fun convert(s: Post): List<TextItem> {
    return mutableListOf(
      TextItem(s.title), TextItem(s.text)
    )
  }
})
```



#### addAll

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
adapter.addAll(posts, object : AnyAdapter.IEachConverter<Post, PostItem> {
  override fun convert(s: Post, position: Int): PostItem {
    return PostItem(s)
  }
})
/* With lamada
* adapter.addAll(posts) { s, position ->
	PostItem(s)
}
*/
```



### Remove

**single-remove** 

Use  `remove(Int)` or `remove(Item)`.

**multiple-remove**

For removing item collection, use `removeAll(Collection<Item>)`;

For removing by conditions, use `removeBy(IFilter)`;

For removing by item type and conditions, use `removeBy(Class<Item>, IFilter)`;

For removing by item type, use `removeAll(Class<Item>)`;

For clearing all, use `clear`;



### Replace

`replace(position, item)`.



### FilterRun

`filterRun(IFilter, IRun)` - execute `IRun` if accepted by `IFilter`;

`filterRun(Class<Item>, IFilter, IRun)` - execute `IRun` if item is an instance of `Class<Item>` and accepted by `IFilter`;



### Selector

There are 2 build-in selectors, `SingleSelector` and `MultipleSelector`. 

You can get selector instance by `singleSelectorFor(Class<Item>)` or `multipleSelectorFor(Class<Item>)`. This will make `SingleSelector` and `MultipleSelector` instance for the first time called. 

>  Before do select operation, make sure that your item class support select. Override **supportSelect** function and return true.

Call `begin()` befor call `select` that make sure the selector is in select mode.

```kotlin
val selector = adapter.singleSelectorFor(Post::class.java)
selector.begin()
selector.select(0) // Select the 0 item
selector.remove() // Remove the selected item
```

After operations, call `end()`.



### Sort

```kotlin
adapter.sortWith(Comparator<SongItem>{ o1, o2 ->
    o1.source().title.compareTo(o2.source().title)
})
```





### OnClick and OnLongClick

**OnClick**

```kotlin
adapter.setOnClickFor(PostItem::class.java, object : OnClick<PostItem> {
  override fun getClickableIds() {
    return intArrayOf(0, R.id.post_title) // 0 for the itemView
  }
  override fun onClick(view: View, item: PostItem, position: Int, adapter: AnyAdapter) {
    when(view.id) {
      R.id.post_title -> {
        
      }
      else -> {
        // Do item click here
      }
    }
  }
})
```

If you just want to set click event to item view, you can use `OnItemClick` directly. No need to override *getClickableIds*.



**OnLongClick**

```kotlin
adapter.setOnLongClickFor(PostItem::class.java, object : OnClick<PostItem> {
  override fun getLongClickableIds() {
    return intArrayOf(0, R.id.post_title) // 0 for the itemView
  }
  override fun onLongClick(view: View, item: PostItem, position: Int, adapter: AnyAdapter): Boolean {
    when(view.id) {
      R.id.post_title -> {
        
      }
      else -> {
        // Do item click here
      }
    }
    return true
  }
})
```

If you just want to set click event to item view, you can use `OnItemLongClick` directly. No need to override *getLongClickableIds*.

> Another alternative choice is `OnItemEvent` for both `OnItemClick` and `OnItemLongClick`.

Also, you can set events when you creating the adapter instance.

```kotlin
private val adapter = AutoFooterAdapter(FooterItem(Footer()))
        .withOnClicks(
            object : AbsOnItemClick<SongItem>(SongItem::class.java) {
                override fun onItemClick(
                    view: View,
                    item: SongItem,
                    position: Int,
                    adapter: AnyAdapter
                ) {
                    TODO("Not yet implemented")
                }
            }
        )
        .withOnLongClicks(
            
        )
```

# One more thing!!!

You can use [AnyAdapterPlugin](https://github.com/boybeak/AnyAdapterPlugin) to generate `item` and `holder` pair, and add binding to holder class if you're using viewBinding.

<img src="https://github.com/boybeak/AnyAdapter/blob/master/menu_create_item_and_holder.png" width="249" height="645"/>

<img src="https://github.com/boybeak/AnyAdapter/blob/master/any_adapter_plugin.png" width="426" height="284"/>

<img src="https://github.com/boybeak/AnyAdapter/blob/master/menu_create_binding.png" width="311" height="484"/>

