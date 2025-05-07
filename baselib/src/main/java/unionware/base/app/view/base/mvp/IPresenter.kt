package unionware.base.app.view.base.mvp



interface IPresenter<V>{
    fun attach(view: V?)

    fun detach()
}