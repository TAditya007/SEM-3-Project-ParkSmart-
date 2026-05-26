(function () {
    const toggle = document.querySelector("[data-theme-toggle]");
    const root = document.documentElement;
    let theme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  
    function applyTheme(value) {
      root.setAttribute("data-theme", value);
      if (toggle) {
        toggle.innerHTML = value === "dark"
          ? "<span>Light Mode</span>"
          : "<span>Dark Mode</span>";
        toggle.setAttribute(
          "aria-label",
          value === "dark" ? "Switch to light mode" : "Switch to dark mode"
        );
      }
    }
  
    applyTheme(theme);
  
    if (toggle) {
      toggle.addEventListener("click", function () {
        theme = theme === "dark" ? "light" : "dark";
        applyTheme(theme);
      });
    }
  
    document.querySelectorAll(".shot-card img").forEach(function (img) {
      img.addEventListener("error", function () {
        const fallback = document.createElement("div");
        fallback.className = "missing-shot";
        fallback.innerHTML = `
          <div class="missing-shot-inner">
            <strong>Screenshot missing</strong>
            <p>This image could not be loaded by GitHub Pages.</p>
            <small>Expected path: <code>${img.getAttribute("src")}</code></small>
          </div>
        `;
        img.replaceWith(fallback);
      });
    });
  })();