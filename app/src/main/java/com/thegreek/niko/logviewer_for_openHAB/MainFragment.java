package com.thegreek.niko.logviewer_for_openHAB;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.TextView;

import androidx.fragment.app.Fragment;

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

        hostnameIPAddress = v.findViewById(R.id.hostnameIPAddress);
        hostnameIPAddressEdit = v.findViewById(R.id.hostnameIPAddressEdit);
        hostnameIPAddressCheck = v.findViewById(R.id.hostnameIPAddressCheck);
        port = v.findViewById(R.id.port);
        portEdit = v.findViewById(R.id.portEdit);
        portCheck = v.findViewById(R.id.portCheck);
        linkView = v.findViewById(R.id.linkView);
        connectButton = v.findViewById(R.id.connectButton);
        connectCheck = v.findViewById(R.id.connectCheck);
        TextView privacy_policy_text = v.findViewById(R.id.privacyPolicy);
        TextView terms_of_use_text = v.findViewById(R.id.termsOfUse);
        TextView credits_text = v.findViewById(R.id.credits);

        mySPR = v.getContext().getSharedPreferences("Speicherstand", 0);
        editor = mySPR.edit();

        statusWiederherstellung();

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
                        getFragmentManager().beginTransaction()
                                .replace(R.id.start, new WebViewFragment())
                                .addToBackStack(null)
                                .commit();

                        if (view != null) {
                            InputMethodManager imm = (InputMethodManager) view.getContext().getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
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

        privacy_policy_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent privacy_policy_intent = new Intent(getContext(), PrivacyPolicy.class);
                view.getContext().startActivity(privacy_policy_intent);
            }
        });

        terms_of_use_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terms_of_use_intent = new Intent(getContext(), TermsOfUse.class);
                view.getContext().startActivity(terms_of_use_intent);
            }
        });

        credits_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent terms_of_use_intent = new Intent(getContext(), Credits.class);
                view.getContext().startActivity(terms_of_use_intent);
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
        connectCheck.setChecked(mySPR.getBoolean("connectCheck", false));

        if (mySPR.getBoolean("autoStart", false) && connectCheck.isChecked()) {
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
