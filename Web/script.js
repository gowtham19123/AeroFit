document.addEventListener("DOMContentLoaded", fetchMembers);

document.getElementById("memberForm").addEventListener("submit", async function(e) {
    e.preventDefault();
    const name = document.getElementById("name").value;
    const plan = document.getElementById("plan").value;
    const duration = document.getElementById("duration").value;
    const msg = document.getElementById("statusMessage");

    const formData = `name=${encodeURIComponent(name)}&plan=${encodeURIComponent(plan)}&duration=${encodeURIComponent(duration)}`;

    try {
        const response = await fetch("/addMember", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData
        });
        const text = await response.text();
        msg.innerText = text;
        msg.style.color = response.ok ? "#28a745" : "#dc3545";
        
        if (response.ok) {
            document.getElementById("memberForm").reset();
            fetchMembers();
        }
    } catch (err) {
        msg.innerText = "Error linking to server.";
        msg.style.color = "#dc3545";
    }
});

async function fetchMembers() {
    try {
        const response = await fetch("/members");
        const members = await response.json();
        const tbody = document.querySelector("#membersTable tbody");
        tbody.innerHTML = "";
        
        members.forEach(m => {
            tbody.innerHTML += `<tr>
                <td>${m.id}</td>
                <td>${m.name}</td>
                <td>${m.plan}</td>
                <td>${m.duration}</td>
            </tr>`;
        });
    } catch (err) {
        console.error("Failed to load roster data", err);
    }
}
