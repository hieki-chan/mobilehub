document.addEventListener("DOMContentLoaded", () => {
  loadPage('pages/home/home.html'); // Default page
});


const navItems = document.querySelectorAll('.nav-item');
const mainContent = document.getElementById('main-content');

navItems.forEach(item => {
    item.addEventListener('click', async () => {
        
        // Bỏ active cũ
        navItems.forEach(i => i.classList.remove('active'));
        item.classList.add('active'); // Set active mới

        const page = item.getAttribute('data-page');
        if (!page) return;

        try {
            //const res = await fetch(page);
            //const html = await res.text();
            //mainContent.innerHTML = html;
            loadPage(page)
        } catch (err) {
            mainContent.innerHTML = `<p style="color:red;">Error loading page: ${err.message}</p>`;
        }
    });
});


async function loadPage(page) {
  const contentDiv = document.getElementById('main-content');

  try {
    const res = await fetch(page);
    if (!res.ok) throw new Error("Page not found");

    const html = await res.text();
    contentDiv.innerHTML = html;

    const jsFile = page.replace('.html', '.js'); // pages/
    loadScript(jsFile);

  } catch (err) {
    contentDiv.innerHTML = `<p style="color:red;">Error loading page: ${err.message}</p>`;
  }
}

async function loadScript(src) {
    let old = document.querySelector(`script[data-dynamic]`);
    if (old) old.remove();

    try {
        const res = await fetch(src, { method: 'HEAD' });
        if (!res.ok) {
            console.warn(`⚠️ Script not found: ${src}`);
            return;
        }
    } catch (e) {
        console.error(`⚠️ Error checking script: ${src}`, e);
        return;
    }

    let script = document.createElement('script');
    script.src = src;
    script.setAttribute('data-dynamic', 'true');
    document.body.appendChild(script);
}
