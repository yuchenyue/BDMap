package com.example.bdmap.presenter;

import android.content.Context;
import android.widget.Toast;

import com.example.bdmap.contract.MainContract;
import com.example.bdmap.model.MainModel;

public class Presenter implements MainContract.MainPresenter {
    private  Context context;
    private MainContract.MainView mainView;
    private MainModel mainModel;

    public Presenter(Context context,MainContract.MainView mainView){
        this.context = context;
        this.mainView = mainView;
        mainModel = new MainModel();
    }

    public void tos(String ms){
        Toast.makeText(context,"MVP提示"+ms,Toast.LENGTH_SHORT).show();
    }

}
