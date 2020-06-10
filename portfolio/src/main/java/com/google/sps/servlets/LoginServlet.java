
// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.util.*;

import com.google.appengine.api.users.*;

import com.google.gson.Gson;

import java.io.IOException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.*;

/** Login handling servlet */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
  private final static Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    UserService userService = UserServiceFactory.getUserService();

    User user;
    if (userService.isUserLoggedIn()) {
      String userEmail = userService.getCurrentUser().getEmail();
      String nickname = userService.getCurrentUser().getNickname();
      String urlToRedirectToAfterUserLogsOut = "/";
      String logoutUrl = userService.createLogoutURL(urlToRedirectToAfterUserLogsOut);

      user = new User(true, null, userEmail, nickname, logoutUrl);
    } else {
      String urlToRedirectToAfterUserLogsIn = "/";
      String loginUrl = userService.createLoginURL(urlToRedirectToAfterUserLogsIn);

      user = new User(false, loginUrl, null, null, null);
    }

    response.getWriter().println(gson.toJson(user));
  }

  private static class User {
    private boolean loggedIn;
    private String loginUrl;
    private String email;
    private String nickname;
    private String logoutUrl;

    private User(boolean loggedIn, String loginUrl, String email, String nickname, String logoutUrl) {
      this.loggedIn = loggedIn;
      this.loginUrl = loginUrl;
      this.email = email;
      this.nickname = nickname;
      this.logoutUrl = logoutUrl;
    }
  }
}
