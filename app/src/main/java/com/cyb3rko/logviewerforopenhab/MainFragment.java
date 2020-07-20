package com.cyb3rko.logviewerforopenhab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class MainFragment extends Fragment {
    private Button connectButton;
    private CheckBox connectCheck;
    private CheckBox hostnameIPAddressCheck;
    private CheckBox portCheck;
    private EditText hostnameIPAddress;
    private EditText port;
    private ImageButton hostnameIPAddressEdit;
    private ImageButton portEdit;
    private ImageView orientation;
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;
    private String hostnameIPAddressString;
    private String link;
    private TextView about;
    private TextView linkView;
    private TextView endUserConsent;

    private int portInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);

        connectButton = v.findViewById(R.id.connect_button);
        connectCheck = v.findViewById(R.id.connect_check);
        hostnameIPAddressCheck = v.findViewById(R.id.hostname_ip_address_check);
        portCheck = v.findViewById(R.id.port_check);
        hostnameIPAddress = v.findViewById(R.id.hostname_ip_address);
        port = v.findViewById(R.id.port);
        hostnameIPAddressEdit = v.findViewById(R.id.hostname_ip_address_edit);
        portEdit = v.findViewById(R.id.port_edit);
        orientation = v.findViewById(R.id.imageView);
        linkView = v.findViewById(R.id.link_view);
        about = v.findViewById(R.id.about);
        endUserConsent = v.findViewById(R.id.end_user_consent);
        TextView versionView = v.findViewById(R.id.version_view);

        // load save file and its editor
        mySPR = v.getContext().getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        // restore set orientation
        Objects.requireNonNull(getActivity()).setRequestedOrientation(mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED));

        // restore last status
        statusRestoring();

        // show version
        versionView.setText(BuildConfig.VERSION_NAME);

        // set onclick listeners
        setOrientationIconClickListener();
        setConnectButtonClickListener(v);
        setEditButtonClickListener(hostnameIPAddressEdit, hostnameIPAddress, portEdit, hostnameIPAddressCheck);
        setEditButtonClickListener(portEdit, port, hostnameIPAddressEdit, portCheck);
        setEndUserConsentClickListener();
        setAboutClickListener();
        setConnectCheckClickListener();

        // show view
        return v;
    }

    // if view is ready restore set orientation
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setRequestedOrientation(mySPR.getInt("orientation", 0));
        super.onViewCreated(view, savedInstanceState);
    }

    // restore last status
    private void statusRestoring() {
        // restore chechbox status
        connectCheck.setChecked(mySPR.getBoolean("connectCheck", false));

        // check if orientation was recently changed
        if (!mySPR.getBoolean("tempDisableStart", false)) {
            // check if autoStart is enabled
            if (mySPR.getBoolean("autoStart", false) && connectCheck.isChecked()) {
                // open logview
                assert getFragmentManager() != null;
                getFragmentManager().beginTransaction()
                        .replace(R.id.start, new WebViewFragment())
                        .addToBackStack(null)
                        .commit();
                editor.putBoolean("connected", true).apply();

                // show toast
                Toasty.info(Objects.requireNonNull(getContext()), getString(R.string.connecting), Toasty.LENGTH_SHORT).show();
            }
        } else {
            // disable temporary lock after orientation was changed
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    editor.putBoolean("tempDisableStart", false).apply();
                }
            };
            new Handler().postDelayed(runnable, 10);
        }

        // set correct orientation icon
        setOrientationIcon();

        // restore textbox status
        if (mySPR.getString("hostnameIPAddressString", "").equals("") || mySPR.getString("hostnameIPAddressString", "").equals("0")) {
            hostnameIPAddress.setText("");
            hostnameIPAddress.setEnabled(true);
        } else {
            hostnameIPAddress.setText(mySPR.getString("hostnameIPAddressString", "0"));
            hostnameIPAddressString = mySPR.getString("hostnameIPAddressString", "0");
            hostnameIPAddress.setEnabled(false);
        }

        // restore checkbox status
        hostnameIPAddressCheck.setChecked(mySPR.getBoolean("hostnameIPAddressCheck", true));

        // restore textbox status
        if (mySPR.getInt("portInt", 0) == 0) {
            port.setText("");
            hostnameIPAddress.setEnabled(true);
        } else {
            port.setText(String.valueOf(mySPR.getInt("portInt", 0)));
            port.setEnabled(false);
            portInt = mySPR.getInt("portInt", 0);
        }

        // restore checkbox status
        portCheck.setChecked(mySPR.getBoolean("portCheck", true));

        // check if connect was clicked and restore last status
        if (!hostnameIPAddress.getText().toString().isEmpty() && !port.getText().toString().isEmpty()) {
            linkGeneration();
            hostnameIPAddressEdit.setVisibility(View.VISIBLE);
            portEdit.setVisibility(View.VISIBLE);
            hostnameIPAddressCheck.setVisibility(View.INVISIBLE);
            portCheck.setVisibility(View.INVISIBLE);
            connectCheck.setVisibility(View.VISIBLE);
            connectButton.setText(getString(R.string.connect_button_2));
        }
    }

    // set correct orientation icon
    private void setOrientationIcon() {
        switch (mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)) {
            case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                orientation.setImageResource(R.drawable._icon_landscape_orientation);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                orientation.setImageResource(R.drawable._icon_portrait_orientation);
                break;
            case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                orientation.setImageResource(R.drawable._icon_auto_orientation);
                break;
            default:
                break;
        }
    }

    // generate and show new link according to user inputs
    private void linkGeneration() {
        hostnameIPAddressString = hostnameIPAddress.getText().toString();

        if (!port.getText().toString().isEmpty()) {
            editor.putInt("portInt", Integer.parseInt(port.getText().toString()));
        } else {
            editor.putInt("portInt", 9001);
        }

        link = "http://" + hostnameIPAddress.getText().toString() + ":" + portInt;
        linkView.setText(link);
    }

    // onClickListener for connect button
    private void setConnectButtonClickListener(final View v) {
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // if both input fields are not empty
                if (!hostnameIPAddress.getText().toString().isEmpty()) {
                    // if connect button was not clicked before
                    if (linkView.getText().toString().isEmpty()) {
                        // generate new link
                        linkGeneration();

                        // switch all elements
                        hostnameIPAddress.setEnabled(false);
                        hostnameIPAddressEdit.setVisibility(View.VISIBLE);
                        port.setEnabled(false);
                        portEdit.setVisibility(View.VISIBLE);
                        hostnameIPAddressCheck.setVisibility(View.INVISIBLE);
                        portCheck.setVisibility(View.INVISIBLE);
                        connectCheck.setVisibility(View.VISIBLE);

                        // store values if user wants to
                        if (hostnameIPAddressCheck.isChecked()) {
                            editor.putString("hostnameIPAddressString", hostnameIPAddressString);
                            editor.putBoolean("hostnameIPAddressCheck", true).apply();
                        } else {
                            editor.putString("hostnameIPAddressString", "");
                            editor.putBoolean("hostnameIPAddressCheck", false).apply();
                        }

                        // store values if user wants to
                        if (portCheck.isChecked()) {
                            // check if user entered hostname
                            if (!port.getText().toString().isEmpty()) {
                                editor.putInt("portInt", portInt);
                            } else {
                                port.setText(String.valueOf(9001));
                                editor.putInt("portInt", 9001);
                            }
                            editor.putBoolean("portCheck", true).apply();
                        } else {
                            editor.putInt("portInt", 0);
                            editor.putBoolean("portCheck", false).apply();
                        }

                        // store link
                        editor.putString("link", link).apply();

                        // change button text
                        connectButton.setText(getString(R.string.connect_button_2));
                    } else {
                        // open logview
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction()
                                .replace(R.id.start, new WebViewFragment())
                                .addToBackStack(null)
                                .commit();

                        editor.putBoolean("connected", true).apply();
                        // close keyboard
                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        // show toast
                        Toasty.info(v.getContext(), getString(R.string.connecting), Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    // show error if one field or both fields are empty
                    Toasty.error(v.getContext(), getString(R.string.error_fill_out), Toasty.LENGTH_LONG).show();
                }
            }
        });
    }

    // onClickListener for orientation icon
    private void setOrientationIconClickListener() {
        orientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newOrientation = 0;
                String newOrientationName = "";

                // check current orientation and define new orientation
                switch (mySPR.getInt("orientation", ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)) {
                    case ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE:
                        newOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                        newOrientationName = "portrait";
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_PORTRAIT:
                        newOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED;
                        newOrientationName = "auto";
                        break;
                    case ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED:
                        newOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                        newOrientationName = "landscape";
                        break;
                    default:
                        break;
                }

                // store orientation, change icon and change orientation
                editor.putInt("orientation", newOrientation);
                editor.putBoolean("tempDisableStart", true).apply();
                setOrientationIcon();
                Objects.requireNonNull(getActivity()).setRequestedOrientation(newOrientation);

                // show toast
                Toasty.info(Objects.requireNonNull(getContext()), String.format(getString(R.string.orientation_changed), newOrientationName), Toasty.LENGTH_SHORT).show();
            }
        });
    }

    // onClickListener for both edit buttons
    private void setEditButtonClickListener(final ImageButton imageButton, final TextView textView, final ImageButton imageButton2, final CheckBox checkBox) {
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // switch all elements
                textView.setEnabled(true);
                imageButton.setVisibility(View.INVISIBLE);
                imageButton2.setVisibility(View.INVISIBLE);
                checkBox.setVisibility(View.VISIBLE);
                connectCheck.setVisibility(View.INVISIBLE);
                linkView.setText("");
                connectButton.setText(getString(R.string.connect_button_1));
            }
        });
    }

    // onClickListener for end user consent textview
    private void setEndUserConsentClickListener() {
        endUserConsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start end user consent class (and show second dialog type ("false"))
                EndUserConsent.dialogType = false;
                EndUserConsent endUserConsent = new EndUserConsent();
                endUserConsent.setCancelable(true);
                endUserConsent.show(getChildFragmentManager(), getClass().getName());
            }
        });
    }

    // onClickListener for about textview
    private void setAboutClickListener() {
        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start about class
                view.getContext().startActivity(new Intent(getContext(), About.class));
            }
        });
    }

    // onClickListener for connect checkbox
    private void setConnectCheckClickListener() {
        connectCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // store values
                editor.putBoolean("connectCheck", connectCheck.isChecked());
                editor.putBoolean("autoStart", connectCheck.isChecked()).apply();
            }
        });
    }
}
