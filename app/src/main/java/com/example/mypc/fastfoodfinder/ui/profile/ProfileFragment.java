package com.example.mypc.fastfoodfinder.ui.profile;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mypc.fastfoodfinder.R;
import com.example.mypc.fastfoodfinder.dialog.DialogCreateNewList;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {

    ImageView ivCoverImage;
    Button btnUpdateCoverImage;
    DialogUpdateCoverImage mDialog;
    DialogCreateNewList mDialogCreate;
    CircleImageView civCreate;
    CardView viewpoint, viewpoint2, cvCreate;


    public static ProfileFragment newInstance(){
        Bundle args = new Bundle();
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        ivCoverImage = (ImageView) rootView.findViewById(R.id.ivCoverImage);
        btnUpdateCoverImage = (Button) rootView.findViewById(R.id.btnUpdateCoverImage);
        civCreate = (CircleImageView) rootView.findViewById(R.id.ivCreate);
        viewpoint = (CardView) rootView.findViewById(R.id.viewPoint);
        viewpoint2 = (CardView) rootView.findViewById(R.id.viewPoint2);
        cvCreate = (CardView) rootView.findViewById(R.id.cvCreateNew);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnUpdateCoverImage.setVisibility(View.VISIBLE);
            }
        });

        btnUpdateCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(mDialog);
                btnUpdateCoverImage.setVisibility(View.GONE);
            }
        });

        civCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                mDialogCreate = DialogCreateNewList.newInstance();
                mDialogCreate.show(getFragmentManager(),"");
                mDialogCreate.setOnButtonClickListener(new DialogCreateNewList.OnCreateListListener() {
                    @Override
                    public void OnButtonClick(String name, String description) {
                        LayoutInflater newCardView = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        CardView cardView = (CardView) newCardView.inflate(R.layout.cardview_new_list,null);
                        TextView tvName = (TextView) cardView.findViewById(R.id.tvNameList);
                        TextView tvDescription = (TextView) cardView.findViewById(R.id.tvDescription);
                        tvName.setText(name);
                        if (description.length()>0)
                        tvDescription.setText(description+ "\n0 Places ");

                        ViewGroup insertPoint = (ViewGroup) viewpoint;
                        insertPoint.addView(cardView,0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        cvCreate.setVisibility(View.GONE);
                        LayoutInflater newCardView2 = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        CardView cardView2 = (CardView) newCardView2.inflate(R.layout.cardview_create_new_list,null);
                        ViewGroup insertPoint2 = (ViewGroup) viewpoint2;
                        insertPoint2.addView(cardView2,0, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        Toast.makeText(getContext(),"top1 = "+ String.valueOf(viewpoint2.getTop())+ "bottom1 = "+ String.valueOf(viewpoint2.getBottom()),Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }

    public void showDialog(DialogUpdateCoverImage dialog){
        dialog = DialogUpdateCoverImage.newInstance();
        dialog.show(getFragmentManager(),"");
        dialog.setOnButtonClickListener(new DialogUpdateCoverImage.OnButtonClickListener() {
            @Override
            public void onButtonClickListener(int Id) {
                if (Id!=0)
                    ivCoverImage.setImageResource(Id);
            }
        });


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem item = menu.findItem(R.id.action_search);
        item.setVisible(false);
    }

}
