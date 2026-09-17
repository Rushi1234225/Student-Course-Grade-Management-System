/* ==========================================================
   Ledger frontend — talks to the Spring Boot REST API at /api.
   Every network call goes through apiCall(), which centralizes
   error handling so a failed request always surfaces a readable
   message instead of breaking the page (mirrors the backend's
   GlobalExceptionHandler philosophy on the client side).
   ========================================================== */

const API = "/api";

// ---------- generic fetch wrapper with graceful error handling ----------
async function apiCall(path, options = {}) {
  try {
    const res = await fetch(API + path, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });

    if (res.status === 204) return null; // no content (DELETE)

    let body = null;
    try { body = await res.json(); } catch (_) { /* empty body */ }

    if (!res.ok) {
      const message = body?.message || `Request failed with status ${res.status}`;
      const err = new Error(message);
      err.details = body?.details;
      throw err;
    }
    return body;
  } catch (networkErr) {
    if (networkErr instanceof TypeError) {
      throw new Error("Could not reach the API. Is the backend running on port 8080?");
    }
    throw networkErr;
  }
}

// ---------- toast ----------
let toastTimer;
function toast(message, type = "success") {
  const el = document.getElementById("toast");
  el.textContent = message;
  el.className = `toast show ${type}`;
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove("show"), 3800);
}

function showFormMsg(id, message, type) {
  const el = document.getElementById(id);
  el.textContent = message;
  el.className = `form-msg ${type}`;
}

// ---------- tabs ----------
document.querySelectorAll(".tab").forEach(btn => {
  btn.addEventListener("click", () => {
    document.querySelectorAll(".tab").forEach(b => { b.classList.remove("active"); b.setAttribute("aria-selected", "false"); });
    document.querySelectorAll(".panel").forEach(p => p.classList.remove("active"));
    btn.classList.add("active");
    btn.setAttribute("aria-selected", "true");
    document.getElementById(`panel-${btn.dataset.tab}`).classList.add("active");
  });
});

// ---------- health check ----------
async function checkHealth() {
  const dot = document.getElementById("statusDot");
  const text = document.getElementById("statusText");
  try {
    await apiCall("/health");
    dot.className = "status-dot up";
    text.textContent = "API online";
  } catch {
    dot.className = "status-dot down";
    text.textContent = "API unreachable";
  }
}

// ================= STUDENTS =================
const studentForm = document.getElementById("studentForm");
let studentsCache = [];

async function loadStudents() {
  try {
    studentsCache = await apiCall("/students");
    renderStudents();
    populateStudentSelect();
  } catch (e) {
    toast(e.message, "error");
  }
}

function renderStudents() {
  const tbody = document.getElementById("studentsTbody");
  const empty = document.getElementById("studentsEmpty");
  document.getElementById("studentCount").textContent = `${studentsCache.length} student${studentsCache.length === 1 ? "" : "s"}`;
  tbody.innerHTML = "";
  empty.hidden = studentsCache.length > 0;

  studentsCache.forEach(s => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="mono">${escapeHtml(s.registrationNumber)}</td>
      <td>${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)}</td>
      <td>${escapeHtml(s.major || "—")}</td>
      <td class="mono">${s.gpa != null ? s.gpa.toFixed(2) : "—"}</td>
      <td class="row-actions">
        <button class="icon-btn" data-edit="${s.id}">Edit</button>
        <button class="icon-btn" data-delete="${s.id}">Delete</button>
      </td>`;
    tbody.appendChild(tr);
  });

  tbody.querySelectorAll("[data-edit]").forEach(b => b.addEventListener("click", () => editStudent(b.dataset.edit)));
  tbody.querySelectorAll("[data-delete]").forEach(b => b.addEventListener("click", () => deleteStudent(b.dataset.delete)));
}

function editStudent(id) {
  const s = studentsCache.find(x => String(x.id) === String(id));
  if (!s) return;
  document.getElementById("studentId").value = s.id;
  document.getElementById("regNumber").value = s.registrationNumber;
  document.getElementById("firstName").value = s.firstName;
  document.getElementById("lastName").value = s.lastName;
  document.getElementById("email").value = s.email;
  document.getElementById("major").value = s.major || "";
  document.getElementById("dob").value = s.dateOfBirth || "";
  document.getElementById("studentSubmitBtn").textContent = "Save changes";
  showFormMsg("studentMsg", `Editing ${s.firstName} ${s.lastName}`, "success");
}

async function deleteStudent(id) {
  if (!confirm("Delete this student? Their enrollment history will also be removed.")) return;
  try {
    await apiCall(`/students/${id}`, { method: "DELETE" });
    toast("Student deleted.", "success");
    loadStudents();
  } catch (e) {
    toast(e.message, "error");
  }
}

studentForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = document.getElementById("studentId").value;
  const payload = {
    registrationNumber: document.getElementById("regNumber").value.trim(),
    firstName: document.getElementById("firstName").value.trim(),
    lastName: document.getElementById("lastName").value.trim(),
    email: document.getElementById("email").value.trim(),
    major: document.getElementById("major").value.trim() || null,
    dateOfBirth: document.getElementById("dob").value || null,
  };
  try {
    if (id) {
      await apiCall(`/students/${id}`, { method: "PUT", body: JSON.stringify(payload) });
      showFormMsg("studentMsg", "Student updated.", "success");
    } else {
      await apiCall("/students", { method: "POST", body: JSON.stringify(payload) });
      showFormMsg("studentMsg", "Student added.", "success");
    }
    resetStudentForm();
    loadStudents();
  } catch (e) {
    showFormMsg("studentMsg", e.details?.join(" · ") || e.message, "error");
  }
});

function resetStudentForm() {
  studentForm.reset();
  document.getElementById("studentId").value = "";
  document.getElementById("studentSubmitBtn").textContent = "Add student";
}
document.getElementById("studentResetBtn").addEventListener("click", () => {
  resetStudentForm();
  showFormMsg("studentMsg", "", "");
});

// ================= COURSES =================
const courseForm = document.getElementById("courseForm");
let coursesCache = [];

async function loadCourses() {
  try {
    coursesCache = await apiCall("/courses");
    renderCourses();
    populateCourseSelect();
  } catch (e) {
    toast(e.message, "error");
  }
}

function renderCourses() {
  const tbody = document.getElementById("coursesTbody");
  const empty = document.getElementById("coursesEmpty");
  document.getElementById("courseCount").textContent = `${coursesCache.length} course${coursesCache.length === 1 ? "" : "s"}`;
  tbody.innerHTML = "";
  empty.hidden = coursesCache.length > 0;

  coursesCache.forEach(c => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td class="mono">${escapeHtml(c.courseCode)}</td>
      <td>${escapeHtml(c.title)}</td>
      <td class="mono">${c.creditHours}</td>
      <td class="mono">${c.enrolledCount ?? 0}</td>
      <td class="row-actions">
        <button class="icon-btn" data-edit="${c.id}">Edit</button>
        <button class="icon-btn" data-delete="${c.id}">Delete</button>
      </td>`;
    tbody.appendChild(tr);
  });

  tbody.querySelectorAll("[data-edit]").forEach(b => b.addEventListener("click", () => editCourse(b.dataset.edit)));
  tbody.querySelectorAll("[data-delete]").forEach(b => b.addEventListener("click", () => deleteCourse(b.dataset.delete)));
}

function editCourse(id) {
  const c = coursesCache.find(x => String(x.id) === String(id));
  if (!c) return;
  document.getElementById("courseId").value = c.id;
  document.getElementById("courseCode").value = c.courseCode;
  document.getElementById("courseTitle").value = c.title;
  document.getElementById("creditHours").value = c.creditHours;
  document.getElementById("department").value = c.department || "";
  document.getElementById("courseDescription").value = c.description || "";
  document.getElementById("courseSubmitBtn").textContent = "Save changes";
  showFormMsg("courseMsg", `Editing ${c.courseCode}`, "success");
}

async function deleteCourse(id) {
  if (!confirm("Delete this course? Related enrollments will also be removed.")) return;
  try {
    await apiCall(`/courses/${id}`, { method: "DELETE" });
    toast("Course deleted.", "success");
    loadCourses();
  } catch (e) {
    toast(e.message, "error");
  }
}

courseForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const id = document.getElementById("courseId").value;
  const payload = {
    courseCode: document.getElementById("courseCode").value.trim(),
    title: document.getElementById("courseTitle").value.trim(),
    creditHours: parseInt(document.getElementById("creditHours").value, 10),
    department: document.getElementById("department").value.trim() || null,
    description: document.getElementById("courseDescription").value.trim() || null,
  };
  try {
    if (id) {
      await apiCall(`/courses/${id}`, { method: "PUT", body: JSON.stringify(payload) });
      showFormMsg("courseMsg", "Course updated.", "success");
    } else {
      await apiCall("/courses", { method: "POST", body: JSON.stringify(payload) });
      showFormMsg("courseMsg", "Course added.", "success");
    }
    resetCourseForm();
    loadCourses();
  } catch (e) {
    showFormMsg("courseMsg", e.details?.join(" · ") || e.message, "error");
  }
});

function resetCourseForm() {
  courseForm.reset();
  document.getElementById("courseId").value = "";
  document.getElementById("courseSubmitBtn").textContent = "Add course";
}
document.getElementById("courseResetBtn").addEventListener("click", () => {
  resetCourseForm();
  showFormMsg("courseMsg", "", "");
});

// ================= ENROLLMENTS =================
const enrollForm = document.getElementById("enrollForm");
let enrollCache = [];

function populateStudentSelect() {
  const sel = document.getElementById("enrollStudent");
  sel.innerHTML = studentsCache.map(s =>
    `<option value="${s.id}">${escapeHtml(s.firstName)} ${escapeHtml(s.lastName)} (${escapeHtml(s.registrationNumber)})</option>`
  ).join("") || `<option value="">No students yet</option>`;
}
function populateCourseSelect() {
  const sel = document.getElementById("enrollCourse");
  sel.innerHTML = coursesCache.map(c =>
    `<option value="${c.id}">${escapeHtml(c.courseCode)} — ${escapeHtml(c.title)}</option>`
  ).join("") || `<option value="">No courses yet</option>`;
}

async function loadEnrollments() {
  try {
    enrollCache = await apiCall("/enrollments");
    renderEnrollments();
  } catch (e) {
    toast(e.message, "error");
  }
}

function renderEnrollments() {
  const tbody = document.getElementById("enrollTbody");
  const empty = document.getElementById("enrollEmpty");
  document.getElementById("enrollCount").textContent = `${enrollCache.length} entr${enrollCache.length === 1 ? "y" : "ies"}`;
  tbody.innerHTML = "";
  empty.hidden = enrollCache.length > 0;

  enrollCache.forEach(en => {
    const tr = document.createElement("tr");
    tr.innerHTML = `
      <td>${escapeHtml(en.studentName)}</td>
      <td>${escapeHtml(en.courseTitle)}</td>
      <td class="mono">${escapeHtml(en.semester)}</td>
      <td class="grade-cell">${gradeLabel(en.grade)}</td>
      <td class="row-actions">
        <button class="icon-btn" data-delete="${en.id}">Delete</button>
      </td>`;
    tbody.appendChild(tr);
  });

  tbody.querySelectorAll("[data-delete]").forEach(b => b.addEventListener("click", () => deleteEnrollment(b.dataset.delete)));
}

function gradeLabel(g) {
  if (!g || g === "NOT_GRADED") return "—";
  return g.replace("_PLUS", "+").replace("_MINUS", "-");
}

async function deleteEnrollment(id) {
  if (!confirm("Remove this enrollment record?")) return;
  try {
    await apiCall(`/enrollments/${id}`, { method: "DELETE" });
    toast("Enrollment removed.", "success");
    loadEnrollments();
    loadStudents(); // GPA may have changed
  } catch (e) {
    toast(e.message, "error");
  }
}

enrollForm.addEventListener("submit", async (e) => {
  e.preventDefault();
  const payload = {
    studentId: parseInt(document.getElementById("enrollStudent").value, 10),
    courseId: parseInt(document.getElementById("enrollCourse").value, 10),
    semester: document.getElementById("semester").value.trim(),
    grade: document.getElementById("enrollGrade").value,
  };
  try {
    await apiCall("/enrollments", { method: "POST", body: JSON.stringify(payload) });
    showFormMsg("enrollMsg", "Enrollment recorded.", "success");
    enrollForm.reset();
    loadEnrollments();
    loadStudents();
    loadCourses();
  } catch (e) {
    showFormMsg("enrollMsg", e.details?.join(" · ") || e.message, "error");
  }
});

// ---------- utils ----------
function escapeHtml(str) {
  if (str == null) return "";
  return String(str)
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

// ---------- init ----------
checkHealth();
loadStudents();
loadCourses();
loadEnrollments();
setInterval(checkHealth, 15000);
