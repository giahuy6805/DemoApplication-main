// =============================================
// TOGGLE SIDEBAR
// =============================================
const sidebar     = document.getElementById('sidebar');
const mainWrapper = document.getElementById('main-wrapper');
const overlay     = document.getElementById('overlay');

function toggleSidebar() {
    const isMobile = window.innerWidth < 992;
    if (isMobile) {
        sidebar.classList.toggle('mobile-open');
        overlay.classList.toggle('show');
    } else {
        sidebar.classList.toggle('collapsed');
        mainWrapper.classList.toggle('expanded');
    }
}

function closeSidebar() {
    sidebar.classList.remove('mobile-open');
    overlay.classList.remove('show');
}

// Active nav link
document.querySelectorAll('.nav-item a').forEach(link => {
    link.addEventListener('click', function () {
        document.querySelectorAll('.nav-item a').forEach(l => l.classList.remove('active'));
        this.classList.add('active');
    });
});

// =============================================
// MÀU VÀ CẤU HÌNH CHART.JS CHUNG
// =============================================
Chart.defaults.font.family = "'Poppins', sans-serif";
Chart.defaults.font.size   = 12;
Chart.defaults.color       = '#858796';

// =============================================
// MINI CHARTS (Sparkline trong thẻ thống kê)
// =============================================
function createMiniChart(canvasId, data, color) {
    const ctx = document.getElementById(canvasId).getContext('2d');

    // Gradient nền
    const gradient = ctx.createLinearGradient(0, 0, 0, 50);
    gradient.addColorStop(0, color + '40');
    gradient.addColorStop(1, color + '00');

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: data.map((_, i) => i),
            datasets: [{
                data: data,
                borderColor: color,
                borderWidth: 2,
                backgroundColor: gradient,
                fill: true,
                tension: 0.45,
                pointRadius: 0,
                pointHoverRadius: 4,
                pointHoverBorderWidth: 2,
                pointHoverBorderColor: '#fff',
                pointHoverBackgroundColor: color
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            animation: { duration: 1200, easing: 'easeOutQuart' },
            plugins: { legend: { display: false }, tooltip: { enabled: false } },
            scales: {
                x: { display: false },
                y: { display: false }
            }
        }
    });
}

// Khởi tạo 4 mini chart
createMiniChart('miniChart1', [3.2, 3.8, 3.5, 4.0, 3.9, 4.3, 4.5, 4.1, 4.6, 4.82], '#4e73df');
createMiniChart('miniChart2', [280, 310, 295, 330, 320, 355, 340, 365, 370, 386],    '#1cc88a');
createMiniChart('miniChart3', [22, 19, 18, 17, 20, 16, 15, 18, 16, 14],              '#f6c23e');
createMiniChart('miniChart4', [80, 92, 87, 105, 98, 110, 103, 120, 115, 128],        '#36b9cc');

// =============================================
// BIỂU ĐỒ ĐƯỜNG: Xu hướng bán hàng
// =============================================
(function () {
    const ctx = document.getElementById('lineChart').getContext('2d');

    const gradientLaptop = ctx.createLinearGradient(0, 0, 0, 280);
    gradientLaptop.addColorStop(0, 'rgba(78,115,223,0.25)');
    gradientLaptop.addColorStop(1, 'rgba(78,115,223,0)');

    const gradientPhone = ctx.createLinearGradient(0, 0, 0, 280);
    gradientPhone.addColorStop(0, 'rgba(28,200,138,0.25)');
    gradientPhone.addColorStop(1, 'rgba(28,200,138,0)');

    // Dữ liệu 12 tháng (T4/2025 → T3/2026)
    const labels     = ['T4', 'T5', 'T6', 'T7', 'T8', 'T9', 'T10', 'T11', 'T12', 'T1', 'T2', 'T3'];
    const laptopData = [142, 158, 135, 170, 165, 180, 195, 210, 230, 175, 185, 198];
    const phoneData  = [188, 205, 195, 215, 225, 240, 255, 290, 310, 260, 245, 270];

    new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: [
                {
                    label: 'Laptop',
                    data: laptopData,
                    borderColor: '#4e73df',
                    backgroundColor: gradientLaptop,
                    borderWidth: 2.5,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#4e73df',
                    pointBorderWidth: 2,
                    pointHoverRadius: 6
                },
                {
                    label: 'Điện thoại',
                    data: phoneData,
                    borderColor: '#1cc88a',
                    backgroundColor: gradientPhone,
                    borderWidth: 2.5,
                    fill: true,
                    tension: 0.4,
                    pointRadius: 4,
                    pointBackgroundColor: '#fff',
                    pointBorderColor: '#1cc88a',
                    pointBorderWidth: 2,
                    pointHoverRadius: 6
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: { mode: 'index', intersect: false },
            animation: { duration: 1400, easing: 'easeOutQuart' },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#fff',
                    titleColor: '#2d3a6e',
                    bodyColor: '#858796',
                    borderColor: '#e8ecf4',
                    borderWidth: 1,
                    padding: 12,
                    callbacks: {
                        label: ctx => ` ${ctx.dataset.label}: ${ctx.parsed.y} sp`
                    }
                }
            },
            scales: {
                x: {
                    grid: { display: false },
                    border: { display: false },
                    ticks: { font: { size: 11 } }
                },
                y: {
                    grid: { color: '#f0f2f8', drawBorder: false },
                    border: { display: false, dash: [4, 4] },
                    ticks: {
                        font: { size: 11 },
                        callback: val => val + ' sp'
                    }
                }
            }
        }
    });
})();

// =============================================
// BIỂU ĐỒ TRÒN: Tỷ lệ thương hiệu
// =============================================
(function () {
    const ctx = document.getElementById('pieChart').getContext('2d');

    const brands  = ['Apple', 'Dell', 'Samsung', 'ASUS', 'Khác'];
    const data    = [38, 22, 18, 14, 8];
    const colors  = ['#4e73df', '#1cc88a', '#f6c23e', '#e74a3b', '#36b9cc'];
    const hColors = ['#3a5abf', '#0ea36e', '#d4a520', '#c0392b', '#2a9aad'];

    const chart = new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: brands,
            datasets: [{
                data: data,
                backgroundColor: colors,
                hoverBackgroundColor: hColors,
                borderWidth: 3,
                borderColor: '#fff',
                hoverOffset: 8
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            animation: { duration: 1400, easing: 'easeOutBounce' },
            plugins: {
                legend: { display: false },
                tooltip: {
                    backgroundColor: '#fff',
                    titleColor: '#2d3a6e',
                    bodyColor: '#858796',
                    borderColor: '#e8ecf4',
                    borderWidth: 1,
                    padding: 12,
                    callbacks: {
                        label: ctx => ` ${ctx.label}: ${ctx.parsed}%`
                    }
                }
            }
        }
    });

    // Tạo legend tùy chỉnh bên dưới biểu đồ
    const legendContainer = document.getElementById('pieLegend');
    brands.forEach((b, i) => {
        const item = document.createElement('div');
        item.style.cssText = 'display:flex;align-items:center;gap:6px;font-size:.75rem;color:#555;cursor:pointer;transition:opacity .2s';
        item.innerHTML = `<span style="width:10px;height:10px;border-radius:50%;background:${colors[i]};flex-shrink:0"></span>${b} <strong style="color:#2d3a6e">${data[i]}%</strong>`;

        // Hover legend → highlight slice tương ứng
        item.addEventListener('mouseenter', () => {
            const meta = chart.getDatasetMeta(0);
            meta.data.forEach((arc, j) => { arc.options.offset = j === i ? 10 : 0; });
            chart.update('none');
        });

        item.addEventListener('mouseleave', () => {
            const meta = chart.getDatasetMeta(0);
            meta.data.forEach(arc => { arc.options.offset = 0; });
            chart.update('none');
        });

        legendContainer.appendChild(item);
    });
})();

// =============================================
// XỬ LÝ NÚT SỬA / XÓA (demo toast)
// =============================================
document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', function () {
        const name = this.closest('tr').querySelector('.product-name').textContent;
        showToast(`Đang chỉnh sửa: ${name}`, 'primary');
    });
});

document.querySelectorAll('.btn-del').forEach(btn => {
    btn.addEventListener('click', function () {
        const name = this.closest('tr').querySelector('.product-name').textContent;
        if (confirm(`Bạn có chắc muốn xóa "${name}" không?`)) {
            showToast(`Đã xóa: ${name}`, 'danger');
        }
    });
});

// Hàm hiển thị Toast thông báo nhỏ
function showToast(message, type = 'primary') {
    const colorMap = { primary: '#4e73df', success: '#1cc88a', danger: '#e74a3b', warning: '#f6c23e' };
    const color    = colorMap[type] || colorMap.primary;

    const toast = document.createElement('div');
    toast.style.cssText = `
        position:fixed; bottom:24px; right:24px; z-index:9999;
        background:#fff; color:#333;
        padding:12px 20px 12px 16px;
        border-radius:10px; border-left:4px solid ${color};
        box-shadow:0 8px 24px rgba(0,0,0,.12);
        font-size:.82rem; font-family:'Poppins',sans-serif;
        display:flex; align-items:center; gap:10px;
        animation:fadeInUp .3s ease;
        min-width:260px; max-width:340px;
    `;
    toast.innerHTML = `<i class="bi bi-info-circle-fill" style="color:${color};font-size:1rem"></i>${message}`;
    document.body.appendChild(toast);

    setTimeout(() => {
        toast.style.transition = 'opacity .4s, transform .4s';
        toast.style.opacity    = '0';
        toast.style.transform  = 'translateY(10px)';
        setTimeout(() => toast.remove(), 400);
    }, 3000);
}
/* =============================================
   Tự động đóng thông báo Alert sau 3 giây
============================================= */
document.addEventListener("DOMContentLoaded", function() {
    // Tìm tất cả các phần tử alert có nút đóng (btn-close)
    var alertList = document.querySelectorAll('.alert');

    alertList.forEach(function (alert) {
        // Đợi 3 giây (3000ms)
        setTimeout(function () {
            // Sử dụng API của Bootstrap để đóng alert một cách chuyên nghiệp
            var bsAlert = new bootstrap.Alert(alert);
            if (bsAlert) {
                bsAlert.close();
            }
        }, 3000);
    });
});
