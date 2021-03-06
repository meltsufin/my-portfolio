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


function init() {
  getImageUploadUrl();
  fetchComments();
  auth();
}

function getImageUploadUrl(callback) {
  fetch("/blobstore-upload-url")
    .then(response => response.text())
    .then(url => document.getElementById("upload-url").value = url);
}

function uploadImage() {
  const url = document.getElementById("upload-url").value;
  const formData = new FormData();
  const fileField = document.querySelector('input[type="file"]');

  formData.append('image', fileField.files[0]);

  fetch(url, {
    method: 'POST',
    body: formData
  })
  .then(response => response.text())
  .then(result => {
     document.getElementById("uploaded-image").src = result;
     document.getElementById("upload-container").style.display = 'none';
    });
}

function auth() {
  loginContainer = document.getElementById("login-container");
  commentForm = document.getElementById("comment-form");
  fetch("/login")
    .then(response => response.json())
    .then(user => {
      console.log(user);
      if (user.loggedIn) {
        commentForm.style.display = "all";
        loginContainer.innerHTML = "<a href='" + user.logoutUrl + "'>logout</a>";
      } else {
        commentForm.style.display = "none";
        loginContainer.innerHTML = "<a href='" + user.loginUrl + "'>login</a>";
      }
    });
}
/**
 * Adds a random greeting to the page.
 */
function addRandomGreeting() {
  const greetings =
      ['Hello world!', '¡Hola Mundo!', '你好，世界！', 'Bonjour le monde!', 'Привет'];

  // Pick a random greeting.
  const greeting = greetings[Math.floor(Math.random() * greetings.length)];

  // Add it to the page.
  const greetingContainer = document.getElementById('greeting-container');
  greetingContainer.innerText = greeting;
}
/**
 * Fetches comments from server and adds it to the DOM
 */
function fetchComments() {
  commentsNum = document.getElementById('comments-num').value;
  // Fetch data and add it to the data-container div
  fetch('/comments?num=' + commentsNum)
    .then(response => response.json())
    .then(comments => displayComments(comments));
}

function displayComments(comments) {
    const parentDiv = document.getElementById('comments-container');
    parentDiv.innerHTML = ''; // remove all children
    comments.forEach(comment => parentDiv.appendChild(
        createListElement(comment)));
}

/** Creates an <li> element containing text. */
function createListElement(text) {
  const liElement = document.createElement('li');
  liElement.innerText = text;
  return liElement;
}

function deleteAllComments() {
    fetch('/delete-comments', { method: 'POST'})
    .then(response => response.json())
    .then(count => {
      console.log('Deleted ' + count + ' comments.');
      fetchComments();
    });
}