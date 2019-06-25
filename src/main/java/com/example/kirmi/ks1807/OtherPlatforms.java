package com.example.kirmi.ks1807;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import static com.spotify.sdk.android.authentication.LoginActivity.REQUEST_CODE;

public class OtherPlatforms extends AppCompatActivity
{
    String UserID = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platforms);

        UserID = Global.UserID;
    }

    public void button_back(View view)
    {
        if (Global.UserExtraMoodQuestions.equals("Yes"))
        {
            Intent intent = new Intent(OtherPlatforms.this, RegisterSecondPage.class);
            startActivity(intent);
        }
        else
        {
            Intent intent = new Intent(OtherPlatforms.this, Register.class);
            startActivity(intent);
        }
    }

    public void button_LoginSpotify(View view)
    {
        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(BackgroundService.CLIENT_ID,
                AuthenticationResponse.Type.TOKEN, BackgroundService.REDIRECT_URI);
        builder.setScopes(new String[]{"streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        // Check if it's from spotify
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    Toast.makeText(this, "Spotify account connected to the application", Toast.LENGTH_SHORT).show();
                    break;
                case ERROR:
                    // Handle error response
                    Toast.makeText(this, response.getError(), Toast.LENGTH_SHORT).show();
                    break;
                // Other cases, not sure what they are.
                default:
                    Toast.makeText(this, "Connection cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
