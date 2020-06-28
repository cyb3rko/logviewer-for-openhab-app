package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
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
    private SharedPreferences mySPR;
    private SharedPreferences.Editor editor;
    private String hostnameIPAddressString;
    private String link;
    private TextView linkView;

    private int portInt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_main, container, false);

        hostnameIPAddress = v.findViewById(R.id.hostname_ip_address);
        hostnameIPAddressEdit = v.findViewById(R.id.hostname_ip_address_edit);
        hostnameIPAddressCheck = v.findViewById(R.id.hostname_ip_address_check);
        port = v.findViewById(R.id.port);
        portEdit = v.findViewById(R.id.port_edit);
        portCheck = v.findViewById(R.id.port_check);
        linkView = v.findViewById(R.id.link_view);
        connectButton = v.findViewById(R.id.connect_button);
        connectCheck = v.findViewById(R.id.connect_check);
        TextView endUserConsent = v.findViewById(R.id.end_user_consent);
        TextView credits_text = v.findViewById(R.id.credits);
        TextView versionView = v.findViewById(R.id.version_view);
        ImageView settings = v.findViewById(R.id.imageView);

        mySPR = v.getContext().getSharedPreferences("Safe", 0);
        editor = mySPR.edit();
        editor.apply();

        statusWiederherstellung();
        versionView.setText(BuildConfig.VERSION_NAME);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newOrientation = mySPR.getInt("orientation", 0) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                MainActivity.changeOrientation(Objects.requireNonNull(getActivity()), newOrientation);
                editor.putInt("orientation", newOrientation).apply();

                String orientation = mySPR.getInt("orientation", 0) == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE ? "landscape" : "portrait";
                Toasty.info(Objects.requireNonNull(getContext()), "Orientation changed to " + orientation, Toasty.LENGTH_SHORT).show();
            }
        });

        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!hostnameIPAddress.getText().toString().isEmpty() && !port.getText().toString().isEmpty()) {
                    if (linkView.getText().toString().isEmpty()) {
                        linkGenerierung();

                        hostnameIPAddress.setEnabled(false);
                        hostnameIPAddressEdit.setVisibility(View.VISIBLE);
                        port.setEnabled(false);
                        portEdit.setVisibility(View.VISIBLE);
                        hostnameIPAddressCheck.setVisibility(View.INVISIBLE);
                        portCheck.setVisibility(View.INVISIBLE);
                        connectCheck.setVisibility(View.VISIBLE);

                        if (hostnameIPAddressCheck.isChecked()) {
                            editor.putString("hostnameIPAddressString", hostnameIPAddressString);
                            editor.putBoolean("hostnameIPAddressCheck", true).apply();
                        } else {
                            editor.putString("hostnameIPAddressString", "");
                            editor.putBoolean("hostnameIPAddressCheck", false).apply();
                        }

                        if (portCheck.isChecked()) {
                            editor.putInt("portInt", portInt);
                            editor.putBoolean("portCheck", true).apply();
                        } else {
                            editor.putInt("portInt", 0);
                            editor.putBoolean("portCheck", false);
                            editor.apply();
                        }

                        editor.putString("link", link).apply();

                        connectButton.setText(getString(R.string.connect_button_2));
                    } else {
                        assert getFragmentManager() != null;
                        getFragmentManager().beginTransaction()
                                .replace(R.id.start, new WebViewFragment())
                                .addToBackStack(null)
                                .commit();

                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            assert imm != null;
                            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                        }

                        Toasty.info(v.getContext(), getString(R.string.connecting), Toasty.LENGTH_SHORT).show();
                    }
                } else {
                    Toasty.error(v.getContext(), getString(R.string.error_fill_out), Toasty.LENGTH_LONG).show();
                }
            }
        });

        hostnameIPAddressEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hostnameIPAddress.setEnabled(true);
                hostnameIPAddressEdit.setVisibility(View.INVISIBLE);
                portEdit.setVisibility(View.INVISIBLE);
                hostnameIPAddressCheck.setVisibility(View.VISIBLE);
                connectCheck.setVisibility(View.INVISIBLE);

                linkView.setText("");
                connectButton.setText(getString(R.string.connect_button_1));
            }
        });

        portEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                port.setEnabled(true);
                hostnameIPAddressEdit.setVisibility(View.INVISIBLE);
                portEdit.setVisibility(View.INVISIBLE);
                portCheck.setVisibility(View.VISIBLE);
                connectCheck.setVisibility(View.INVISIBLE);

                linkView.setText("");
                connectButton.setText(getString(R.string.connect_button_1));
            }
        });

        endUserConsent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EndUserConsent2 endUserConsent2 = new EndUserConsent2();
                endUserConsent2.setCancelable(true);
                endUserConsent2.show(getChildFragmentManager(), "EndUserConsent2");
            }
        });

        credits_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent termsOfUseIntent = new Intent(getContext(), Credits.class);
                view.getContext().startActivity(termsOfUseIntent);
            }
        });

        connectCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean("connectCheck", connectCheck.isChecked());
                editor.putBoolean("autoStart", connectCheck.isChecked()).apply();
            }
        });

        return v;
    }

    private void statusWiederherstellung() {
        MainActivity.changeOrientation(Objects.requireNonNull(getActivity()), mySPR.getInt("orientation", 0));

        connectCheck.setChecked(mySPR.getBoolean("connectCheck", false));

        if (mySPR.getBoolean("autoStart", false) && connectCheck.isChecked()) {
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction()
                    .replace(R.id.start, new WebViewFragment())
                    .addToBackStack(null)
                    .commit();

            Toasty.info(getActivity().getApplicationContext(), getString(R.string.connecting), Toasty.LENGTH_SHORT).show();

            return;
        }

        if (mySPR.getString("hostnameIPAddressString", "").equals("") || mySPR.getString("hostnameIPAddressString", "").equals("0")) {
            hostnameIPAddress.setText("");
            hostnameIPAddress.setEnabled(true);
        } else {
            hostnameIPAddress.setText(mySPR.getString("hostnameIPAddressString", "0"));
            hostnameIPAddressString = mySPR.getString("hostnameIPAddressString", "0");
            hostnameIPAddress.setEnabled(false);
        }

        hostnameIPAddressCheck.setChecked(mySPR.getBoolean("hostnameIPAddressCheck", true));

        if (mySPR.getInt("portInt", 0) == 0) {
            port.setText("");
            hostnameIPAddress.setEnabled(true);
        } else {
            port.setText(String.valueOf(mySPR.getInt("portInt", 0)));
            port.setEnabled(false);
            portInt = mySPR.getInt("portInt", 0);
        }

        portCheck.setChecked(mySPR.getBoolean("portCheck", true));

        if (!hostnameIPAddress.getText().toString().isEmpty() && !port.getText().toString().isEmpty()) {
            linkGenerierung();

            hostnameIPAddressEdit.setVisibility(View.VISIBLE);
            portEdit.setVisibility(View.VISIBLE);
            hostnameIPAddressCheck.setVisibility(View.INVISIBLE);
            portCheck.setVisibility(View.INVISIBLE);
            connectCheck.setVisibility(View.VISIBLE);

            connectButton.setText(getString(R.string.connect_button_2));
        }
    }

    private void linkGenerierung() {
        hostnameIPAddressString = hostnameIPAddress.getText().toString();
        portInt = Integer.parseInt(port.getText().toString());
        link = "http://" + hostnameIPAddress.getText().toString() + ":" + portInt;
        linkView.setText(link);
    }
}
