package com.qianfeng.jxshop.adapters;

/**
 * Created with IntelliJ IDEA.
 * User: vhly[FR]
 * Date: 15/10/14
 * Email: vhly@163.com
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.qianfeng.jxshop.R;
import com.qianfeng.jxshop.model.CartItem;

import java.util.List;

/**
 * 基本的购物车列表适配器
 */
public class CartAdapter extends BaseAdapter {

    private Context context;

    private List<CartItem> items;

    /**
     * 代表当前 ListView 的显示模式，包含 (0 : 普通模式; 1 : 编辑模式)
     */
    private int listMode;

    /**
     * 当某一个条目通过 checkBox 选中发生变化，回调
     * 的接口
     */
    private CompoundButton.OnCheckedChangeListener itemCheckedListener;

    /**
     * 数量调整按钮的处理
     */
    private View.OnClickListener onCountChangeListener;

    public CartAdapter(Context context, List<CartItem> items) {
        this.context = context;
        this.items = items;
    }

    /**
     * 设置接口，用于条目的选中
     * @param itemCheckedListener
     */
    public void setItemCheckedListener(CompoundButton.OnCheckedChangeListener itemCheckedListener) {
        this.itemCheckedListener = itemCheckedListener;
    }

    /**
     * 用于调整购买数量
     * @param onCountChangeListener
     */
    public void setOnCountChangeListener(View.OnClickListener onCountChangeListener) {
        this.onCountChangeListener = onCountChangeListener;
    }

    @Override
    public int getCount() {
        int ret = 0;

        if (items != null) {
            ret = items.size();
        }

        return ret;
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View ret = null;

        // 1. 视图复用
        if (convertView != null) {
            ret = convertView;
        } else {
            LayoutInflater inflater = LayoutInflater.from(context);
            ret = inflater.inflate(R.layout.cart_item, parent, false);
        }

        // 2. ViewHolder 创建
        ViewHolder holder = (ViewHolder) ret.getTag();
        if (holder == null) {
            holder = new ViewHolder();

            // TODO 设置UI控件
            holder.checkBox = (CheckBox) ret.findViewById(R.id.cart_item_checkbox);

            // TODO 设置 CheckBox 选中变化的事件，选中之后，改变CartItem的内容
            // !!!
            holder.checkBox.setOnCheckedChangeListener(itemCheckedListener);

            holder.imgIcon = (ImageView) ret.findViewById(R.id.cart_item_product_icon);

            holder.txtProductName = (TextView) ret.findViewById(R.id.cart_item_product_name);

            holder.txtProductPrice = (TextView) ret.findViewById(R.id.cart_item_product_price);

            // 加号按钮
            holder.btnInc = ret.findViewById(R.id.cart_item_inc);

            // TODO 事件
            holder.btnInc.setOnClickListener(onCountChangeListener);

            holder.txtCount = (TextView) ret.findViewById(R.id.cart_item_count);

            // 减号按钮
            holder.btnDec = ret.findViewById(R.id.cart_item_dec);

            // 减一件商品
            holder.btnDec.setOnClickListener(onCountChangeListener);

            holder.btnDelete = ret.findViewById(R.id.cart_item_delete);

            ret.setTag(holder);
        }

        // 3. 获取数据
        CartItem cartItem = items.get(position);

        // 4. 显示数据
        String productName = cartItem.getProductName();
        holder.txtProductName.setText(productName);

        // 4.2 显示数量
        int count = cartItem.getCount();
        holder.txtCount.setText(Integer.toString(count));

        // 4.2.1 设置 按钮的 Tag，用于给监听接口返回 当前条目的位置
        holder.btnInc.setTag(position);

        holder.btnDec.setTag(position);


        // 4.3 显示价格
        float price = cartItem.getProductPrice();
        holder.txtProductPrice.setText(Float.toString(price));

        // 4.4 显示图片（暂时保留）

        // 4.5 根据模式，处理 CheckBox

        // 0. 设置CheckBox 的 Tag
        holder.checkBox.setTag(position);


        // 1. 不论任何状态，ViewHolder中的所有控件， 在每一次getView 的时候
        //    都必须设置与刷新
        if (listMode == 1) {

            // 当编辑模式显示的时候，CheckBox 是否选中是依赖于 cartItem的变量的
            boolean isChecked = cartItem.isChecked();
            holder.checkBox.setChecked(isChecked);

            // 只要是编辑模式，那么CheckBox可见
            holder.checkBox.setVisibility(View.VISIBLE);

        } else {

            // 非编辑模式，CheckBox 取消选中
            holder.checkBox.setChecked(false);

            // 注意，当非编辑模式，数据实体中的 checked 也应该变成 false
            holder.checkBox.setVisibility(View.INVISIBLE);

        }

        return ret;
    }

    /**
     * 切换内部变量，进行编辑模式的切换；
     * 因为ListView显示内容的变化需要 使用 getView 方法；
     * 那么 切换模式的时候，让 adapter 进行 notifyDataSetChanged()，
     * 强制触发 getView()
     */
    public void switchEditMode() {
        if (listMode == 1) {
            listMode = 0;
        } else if (listMode == 0) {
            listMode = 1;
        }
        notifyDataSetChanged();
    }

    private static class ViewHolder {

        public CheckBox checkBox;

        public ImageView imgIcon;

        public TextView txtProductName;

        public TextView txtProductPrice;

        // 加号
        public View btnInc;

        // 个数
        public TextView txtCount;

        // 减号
        public View btnDec;

        // 删除
        public View btnDelete;
    }

}
