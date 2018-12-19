/*
 * Copyright (C) 2016 Angad Singh
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.reactnativedocumentpicker.controller.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.reactnativedocumentpicker.R;
import com.reactnativedocumentpicker.controller.NotifyItemChecked;
import com.reactnativedocumentpicker.model.DialogConfigs;
import com.reactnativedocumentpicker.model.DialogProperties;
import com.reactnativedocumentpicker.model.FileListItem;
import com.reactnativedocumentpicker.model.MarkedItemList;
import com.reactnativedocumentpicker.view.FilePickerDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/* <p>
 * Created by Angad Singh on 09-07-2016.
 * </p>
 */

/**
 * Adapter Class that extends {@link BaseAdapter} that is
 * used to populate {@link ListView} with file info.
 */
public class FileListAdapter extends BaseAdapter {
    private ArrayList<FileListItem> listItem;
    private Context context;
    private DialogProperties properties;
    private NotifyItemChecked notifyItemChecked;

    public FileListAdapter(ArrayList<FileListItem> listItem, Context context, DialogProperties properties) {
        this.listItem = listItem;
        this.context = context;
        this.properties = properties;
    }

    @Override
    public int getCount() {
        return listItem.size();
    }

    @Override
    public FileListItem getItem(int i) {
        return listItem.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    @SuppressWarnings("deprecation")
    public View getView(final int i, View view, ViewGroup viewGroup) {
        final ViewHolder holder;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.dialog_file_list_item, viewGroup, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final FileListItem item = listItem.get(i);
        if (MarkedItemList.hasItem(item.getLocation())) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.marked_item_animation);
            view.setAnimation(animation);
        } else {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.unmarked_item_animation);
            view.setAnimation(animation);
        }
        if (item.isDirectory()) {
            holder.type_icon.setImageResource(R.mipmap.ic_type_folder);
            holder.type_icon.setColorFilter(FilePickerDialog.accentColor);

            if (properties.selection_mode == DialogConfigs.SINGLE_MODE) {
                if (properties.selection_type == DialogConfigs.FILE_SELECT) {
                    holder.rbSingleMode.setVisibility(View.INVISIBLE);
                } else {
                    holder.rbSingleMode.setVisibility(View.VISIBLE);
                }
                holder.cbMultiMode.setVisibility(View.GONE);
            } else {
                holder.rbSingleMode.setVisibility(View.GONE);
                if (properties.selection_type == DialogConfigs.FILE_SELECT) {
                    holder.cbMultiMode.setVisibility(View.INVISIBLE);
                } else {
                    holder.cbMultiMode.setVisibility(View.VISIBLE);
                }
            }
        } else {
            holder.type_icon.setImageResource(R.mipmap.ic_type_file);
            holder.type_icon.setColorFilter(FilePickerDialog.accentColor);
            if (properties.selection_mode == DialogConfigs.SINGLE_MODE) {
                if (properties.selection_type == DialogConfigs.DIR_SELECT) {
                    holder.rbSingleMode.setVisibility(View.INVISIBLE);
                } else {
                    holder.rbSingleMode.setVisibility(View.VISIBLE);
                }
                holder.cbMultiMode.setVisibility(View.GONE);
            } else {
                holder.rbSingleMode.setVisibility(View.GONE);
                if (properties.selection_type == DialogConfigs.DIR_SELECT) {
                    holder.cbMultiMode.setVisibility(View.INVISIBLE);
                } else {
                    holder.cbMultiMode.setVisibility(View.VISIBLE);
                }
            }
        }
        holder.type_icon.setContentDescription(item.getFilename());
        holder.name.setText(item.getFilename());
        SimpleDateFormat sdate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat stime = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
        Date date = new Date(item.getTime());
        if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
            holder.type.setText(R.string.label_parent_directory);
        } else {
            holder.type.setText(context.getString(R.string.last_edit) + sdate.format(date) + ", " + stime.format(date));
        }
        if (properties.selection_mode == DialogConfigs.SINGLE_MODE) {
            if (holder.rbSingleMode.getVisibility() == View.VISIBLE) {
                if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
                    holder.rbSingleMode.setVisibility(View.INVISIBLE);
                }
                holder.rbSingleMode.setOnCheckedChangeListener(null);
                holder.rbSingleMode.setChecked(MarkedItemList.hasItem(item.getLocation()));
            }
        } else {
            if (holder.cbMultiMode.getVisibility() == View.VISIBLE) {
                if (i == 0 && item.getFilename().startsWith(context.getString(R.string.label_parent_dir))) {
                    holder.cbMultiMode.setVisibility(View.INVISIBLE);
                }
                holder.cbMultiMode.setOnCheckedChangeListener(null);
                holder.cbMultiMode.setChecked(MarkedItemList.hasItem(item.getLocation()));
            }
        }

        holder.rbSingleMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                MarkedItemList.clearSelectionList();
                item.setMarked(isChecked);
                if (item.isMarked()) {
                    MarkedItemList.addSingleFile(item);
                } else {
                    MarkedItemList.removeSelectedItem(item.getLocation());
                }
                notifyItemChecked.notifyCheckBoxIsClicked();
            }
        });

        holder.cbMultiMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                item.setMarked(!item.isMarked());
                if (item.isMarked()) {
                    MarkedItemList.addSelectedItem(item);
                } else {
                    MarkedItemList.removeSelectedItem(item.getLocation());
                }
                notifyItemChecked.notifyCheckBoxIsClicked();
            }
        });

        return view;
    }

    private class ViewHolder {
        ImageView type_icon;
        TextView name, type;
        AppCompatRadioButton rbSingleMode;
        AppCompatCheckBox cbMultiMode;

        ViewHolder(View itemView) {
            name = itemView.findViewById(R.id.fname);
            type = itemView.findViewById(R.id.ftype);
            type_icon = itemView.findViewById(R.id.image_type);
            rbSingleMode = itemView.findViewById(R.id.rbSingleMode);
            cbMultiMode = itemView.findViewById(R.id.cbMultiMode);

            name.setTextColor(Color.BLACK);
            type.setTextColor(Color.BLACK);
        }
    }

    public void setNotifyItemCheckedListener(NotifyItemChecked notifyItemChecked) {
        this.notifyItemChecked = notifyItemChecked;
    }
}
