# AnyAdapter [ ![Download](https://api.bintray.com/packages/boybeak/nulldreams/any-adapter/images/download.svg) ](https://bintray.com/boybeak/nulldreams/any-adapter/_latestVersion)

## 安装

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

## 使用

```kotlin
val adapter = new AnyAdapter()
// Or you can use FooterAdapter, AutoFooterAdapter
recyclerView.adapter = adapter
```

你的model类必须被 `ItemImpl<T>`所包裹. `T`就是你要展现的数据. AnyAdapter 只接受 `ItemImpl`的子类作为其中的Item, 实际上，继承 `AbsItem`会更方便.

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

这是一个非常典型的 `add` 操作. 再也不用执行 **notifyDataXXXChanged** 操作了. 数据刷新是自动完成的.

另外一个 **one-to-many** 添加函数能够将一个单一数据转换成一组数据添加进来.

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

实际上，你可以使用更为方便快捷的方式，如下:

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

**单个删除**

使用  `remove(Int)` 或者 `remove(Item)`.

**多个删除**

删除数据集合, 使用 `removeAll(Collection<Item>)`;

条件删除, 使用 `removeBy(IFilter)`;

类型条件删除, 使用 `removeBy(Class<Item>, IFilter)`;

类型删除, 使用 `removeAll(Class<Item>)`;

删除所有, 使用 `clear()`;



### Replace

`replace(position, item)`.



### FilterRun

`filterRun(IFilter, IRun)` - 如果`IFilter`返回true, 则执行 `IRun`;

`filterRun(Class<Item>, IFilter, IRun)` - 如果是 `Class<Item>` 类型并且 `IFilter`返回true, 执行 `IRun` ;



### Selector

内置两个选择器, `SingleSelector` 和 `MultipleSelector`. 

你可以通过 `singleSelectorFor(Class<Item>)` 或者 `multipleSelectorFor(Class<Item>)`来获取对应的选择器. 当第一次调用 `SingleSelector` 或者 `MultipleSelector` 的时候，对应的实例将被创建. 

>  在使用选择功能前, 请确保你的item class支持选择. 在你的item class中重写 **supportSelect** 方法并且返回true.

在调用 `select`前，先调用 `begin()`  以确保选择器进去选择状态.

```kotlin
val selector = adapter.singleSelectorFor(Post::class.java)
selector.begin()
selector.select(0) // Select the 0 item
selector.remove() // Remove the selected item
```

执行相关操作后, 调用 `end()`结束选择状态.



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

如果你只想为整个item设置点击事件, 你可以直接使用 `OnItemClick` . 不用再重写 *getClickableIds*.



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

如果你只想为整个item设置长按事件, 你可以直接使用 `OnItemLongClick`. 无需再重写 *getLongClickableIds*.

> 如果点击事件和长按事件都要设置，你可以使用 `OnItemEvent` ，既是 `OnItemClick` 又是 `OnItemLongClick`.

另外一种方法，可以在创建adapter时候直接设置相关事件。

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

你可以使用 [AnyAdapterPlugin](https://github.com/boybeak/AnyAdapterPlugin) 来产生 `item` `holder` 对, 如果你使用了viewBinding，还可以在holder类中，添加对应的binding对象.

<img src="https://github.com/boybeak/AnyAdapter/blob/master/menu_create_item_and_holder.png" width="249" height="645"/>

<img src="https://github.com/boybeak/AnyAdapter/blob/master/any_adapter_plugin.png" width="426" height="284"/>

<img src="https://github.com/boybeak/AnyAdapter/blob/master/menu_create_binding.png" width="311" height="484"/>