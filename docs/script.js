(function () {
    const toggle = document.querySelector("[data-theme-toggle]");
    const root = document.documentElement;
    let theme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light";
  
    function applyTheme(value) {
      root.setAttribute("data-theme", value);
      if (toggle) {
        toggle.innerHTML = value === "dark" ? "<span>Light Mode</span>" : "<span>Dark Mode</span>";
        toggle.setAttribute("aria-label", value === "dark" ? "Switch to light mode" : "Switch to dark mode");
      }
    }
  
    applyTheme(theme);
  
    if (toggle) {
      toggle.addEventListener("click", function () {
        theme = theme === "dark" ? "light" : "dark";
        applyTheme(theme);
      });
    }
  
    document.querySelectorAll('img').forEach(function (img) {
      img.addEventListener('error', function () {
        const wrapper = document.createElement('div');
        wrapper.className = 'image-fallback';
        wrapper.setAttribute('role', 'img');
        wrapper.setAttribute('aria-label', img.alt || 'Image not available');
        wrapper.style.minHeight = '220px';
        wrapper.style.borderRadius = '14px';
        wrapper.style.display = 'grid';
        wrapper.style.placeItems = 'center';
        wrapper.style.textAlign = 'center';
        wrapper.style.padding = '24px';
        wrapper.style.background = 'rgba(255,255,255,0.04)';
        wrapper.style.border = '1px solid rgba(255,255,255,0.12)';
        wrapper.style.color = '#aebbd8';
        wrapper.textContent = 'Screenshot not found. Add the image inside docs/screenshots with the correct file name.';
        img.replaceWith(wrapper);
      });
    });
  })();