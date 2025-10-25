import { useState } from "react";
import { useNavigate } from "react-router-dom";
import API from "../services/api";

export default function LoginForm() {
	const [email, setEmail] = useState("");
	const [password, setPassword] = useState("");
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState("");
	const navigate = useNavigate();

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setLoading(true);
		setError("");
		try {
			const res = await API.post("/token/", { email, password });
			localStorage.setItem("accessToken", res.data.access);
			localStorage.setItem("refreshToken", res.data.refresh);
			navigate("/admin/dashboard");
		} catch (err) {
			console.error("Login error:", err);
			setError("Credenciales incorrectas, intente de nuevo.");
		} finally {
			setLoading(false);
		}
	};

	return (
		<form className="space-y-6" onSubmit={handleSubmit}>
			<div>
				<label className="block text-gray-300 text-sm font-medium mb-2">
					Correo electrónico
				</label>
				<input
					type="email"
					value={email}
					onChange={e => setEmail(e.target.value)}
					className="w-full px-4 py-3 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-gray-500 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
					required
				/>
			</div>
			<div>
				<label className="block text-gray-300 text-sm font-medium mb-2">
					Contraseña
				</label>
				<input
					type="password"
					value={password}
					onChange={e => setPassword(e.target.value)}
					className="w-full px-4 py-3 bg-slate-800 border border-slate-700 rounded-lg text-white placeholder-gray-600 focus:outline-none focus:border-blue-500 focus:ring-1 focus:ring-blue-500 transition"
					required
				/>
			</div>
			{error && <p className="text-red-500 text-sm">{error}</p>}
			<button
				type="submit"
				className="w-full py-3 bg-blue-600 hover:bg-blue-700 text-white font-semibold rounded-lg transition"
				disabled={loading}
			>
				{loading ? "Ingresando..." : "Inciar sesión"}
			</button>
		</form>
	);
}