package com.qianfeng.jxshop;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.alipay.sdk.pay.demo.PayDemoActivity;
import com.qianfeng.jxshop.adapters.CartAdapter;
import com.qianfeng.jxshop.model.CartItem;

import java.util.LinkedList;

public class MainActivity extends Activity implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemClickListener {

    private CartAdapter adapter;
    private LinkedList<CartItem> items;

    /**
     * 总金额
     */
    private TextView txtTotal;

    /**
     * 计算总金额的观察者，检测 Adapter
     */
    private DataSetObserver sumObserver = new DataSetObserver() {
        /**
         * 当 Adapter 的 notifyDataSetChanged 被调用，
         * 那么自动回调这个方法
         */
        @Override
        public void onChanged() {
            // TODO 计算总金额

            double sum = 0;

            for (CartItem item : items) {
                int count = item.getCount();
                float price = item.getProductPrice();

                sum += (price * count);
            }

            txtTotal.setText(sum + "元");

        }

        /**
         * 当 Adpater 调用 notifyDataSetInvalidate() 时候回调
         */
        @Override
        public void onInvalidated() {

        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        txtTotal = (TextView) findViewById(R.id.cart_total_text);

        ListView listView = (ListView) findViewById(R.id.cart_list_view);

        items = new LinkedList<CartItem>();

        //设置实验数据：
        for (int i = 0; i < 30; i++) {
            CartItem item = new CartItem();

            item.setProductName("商品 " + i);
            item.setProductPrice(10.0f);
            item.setCount(1);

            items.add(item);
        }

        adapter = new CartAdapter(this, items);

        // 由Activity来监听 ListView条目内部 eckBox 的选中变化
        adapter.setItemCheckedListener(this);
        // 由Activity来处理数量的变化
        adapter.setOnCountChangeListener(this);

        // 设置 Adapter 的数据变化观察者，
        // 只要 Adapter 的 notifyDataSetChanged 被调用，观察者自动回调

        adapter.registerDataSetObserver(sumObserver);

        listView.setAdapter(adapter);

//        listView.setOnItemClickListener(this);

    }

    @Override
    protected void onDestroy() {

        adapter.unregisterDataSetObserver(sumObserver);

        super.onDestroy();

    }

    /**
     * 点击按钮，切换编辑模式
     * @param view
     */
    public void btnSwitchEditMode(View view) {

        adapter.switchEditMode();

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        Object tag = buttonView.getTag();
        // 获取 Adapter 中设置的 Tag
        if(tag != null && tag instanceof Integer){
            int position = (Integer)tag;

            CartItem cartItem = items.get(position);

            // 数据状态改变；不需要强制 notifyDataSetChanged
            cartItem.setChecked(isChecked);
        }


    }

    @Override
    public void onClick(View v) {
        // 点击接口，用于处理ListView 内部的按钮的点击

        int id = v.getId();

        Object tag = v.getTag();

        switch (id){
            case R.id.cart_item_inc:  // 加1
                if (tag != null && tag instanceof Integer){

                    int position = (Integer)tag;

                    CartItem cartItem = items.get(position);

                    int count = cartItem.getCount();

                    count++;

                    // 设置增加一个数量
                    cartItem.setCount(count);

                    // 强制刷新 Adapter，就会自动更新 数量 TextView
                    adapter.notifyDataSetChanged();
                }
                break;
            case R.id.cart_item_dec:  // 减1
                if (tag != null && tag instanceof Integer){

                    int position = (Integer)tag;

                    CartItem cartItem = items.get(position);

                    int count = cartItem.getCount();

                    count--;

                    if(count > 0) {

                        // 设置增加一个数量
                        cartItem.setCount(count);

                        // 强制刷新 Adapter，就会自动更新 数量 TextView
                        adapter.notifyDataSetChanged();
                    } else{
                        // 对于小于1的情况，可以是不处理，也可以是，删除条目

                    }
                }
                break;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    /**
     * 调用支付宝支付的DemoActivity，来进行实际支付操作
     * @param view
     */
    public void btnPay(View view) {

        //将商品信息  作为参数  传过去

        Intent intent = new Intent(this, PayDemoActivity.class);

        // TODO 传递订单名称以及钱的总数，让  PayDemoActivity 能够正确的显示，以及支付
        startActivity(intent);

    }
}
