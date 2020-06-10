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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;

/** Comments handling servlet */
@WebServlet("/comments")
public class CommentsServlet extends HttpServlet {

  private final Gson gson = new Gson();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json;");
    int num = Integer.parseInt(request.getParameter("num"));
    List<String> comments = loadCommentsFromDatastore(num);
    String json = gson.toJson(comments);
    response.getWriter().println(json);
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");

    UserService userService = UserServiceFactory.getUserService();
    String name = userService.getCurrentUser().getEmail();

    saveCommentToDatastore(name, comment);

    // Redirect back to the HTML page.
    response.sendRedirect("/index.html");
  }

  private void saveCommentToDatastore(String name, String comment) {
    long timestamp = System.currentTimeMillis();

    Entity commentEntity = new Entity("Comment");
    commentEntity.setProperty("name", name);
    commentEntity.setProperty("message", comment);
    commentEntity.setProperty("timestamp", timestamp);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(commentEntity);
  }

  private List<String> loadCommentsFromDatastore(int num) {
    Query query = new Query("Comment")
                .addSort("timestamp", SortDirection.DESCENDING);

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList();

    int count = 0;
    for (Entity entity : results.asIterable()) {
      if (count >= num)
        break;
      String message = entity.getProperty("name") + " says: " + entity.getProperty("message");
      comments.add(message);
      count++;
    }

    return comments;
  }
}
