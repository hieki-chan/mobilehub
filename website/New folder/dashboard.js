// ======================= PIE CHART =======================
new Chart(document.getElementById("pieChart"), {
  type: "doughnut",
  data: {
    labels: ["Iphone", "Samsung", "Oppo", "Remie"],
    datasets: [
      {
        data: [72, 18, 6, 4],
        backgroundColor: ["#f7c948", "#d3d3d3", "#fff6b2", "#eee"],
      },
    ],
  },
  options: {
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: "right",
        labels: { boxWidth: 15, padding: 10 },
      },
    },
    layout: {
      padding: { left: 10, right: 10, top: 10, bottom: 10 },
    },
  },
});

// ======================= LINE CHART (Sales) =======================
new Chart(document.getElementById("lineChart"), {
  type: "line",
  data: {
    labels: [
      "Jan",
      "Feb",
      "Mar",
      "Apr",
      "May",
      "Jun",
      "Jul",
      "Aug",
      "Sep",
      "Oct",
      "Nov",
      "Dec",
    ],
    datasets: [
      {
        label: "Sales",
        data: [0.8, 1.6, 1.2, 2.1, 2.4, 1.0, 1.8, 2.3, 1.7, 2.6, 2.1, 1.5],
        fill: true,
        borderColor: "#f7c948",
        backgroundColor: "rgba(247,201,72,0.3)",
        tension: 0.35,
        pointBackgroundColor: "#f7c948",
        pointBorderWidth: 2,
      },
    ],
  },
  options: {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
        max: 3,
        ticks: {
          callback: (value) => "$" + value.toFixed(1) + "M",
        },
      },
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (ctx) => "Sales: $" + ctx.parsed.y.toFixed(2) + "M",
        },
      },
    },
  },
});

// ======================= BAR CHART (Ads Expense) =======================
new Chart(document.getElementById("barChart"), {
  type: "bar",
  data: {
    labels: ["M", "T", "W", "T", "F", "S", "S"],
    datasets: [
      {
        data: [60, 120, 200, 90, 160, 180, 120],
        backgroundColor: [
          "#000",
          "#f7c948",
          "#000",
          "#f7c948",
          "#000",
          "#f7c948",
          "#000",
        ],
      },
    ],
  },
  options: {
    plugins: { legend: { display: false } },
  },
});

// ======================= LINE CHART (Repayments) =======================
const repayCtx = document.getElementById("repayChart").getContext("2d");
new Chart(repayCtx, {
  type: "line",
  data: {
    labels: ["Tháng 1", "Tháng 4", "Tháng 8", "Tháng 12"],
    datasets: [
      {
        label: "Nợ phải trả",
        data: [0.9, 1.2, 0.8, 1.5],
        fill: true,
        borderColor: "#f7c948",
        backgroundColor: "rgba(247, 201, 72, 0.3)",
        tension: 0.35,
        pointBackgroundColor: "#f7c948",
        pointBorderWidth: 2,
      },
    ],
  },
  options: {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        max: 2,
        ticks: {
          callback: (value) => "$" + value.toFixed(1) + "M",
        },
      },
      x: { grid: { color: "rgba(0,0,0,0.05)" } },
    },
    plugins: {
      legend: { display: false },
      tooltip: {
        callbacks: {
          label: (ctx) => "Nợ: $" + ctx.parsed.y.toFixed(2) + "M",
        },
      },
    },
  },
});
