import { useEffect, useState } from "react";
import API from "../services/api";
import type { User, UserPayload, UserRole } from "../types";

type Mode = "create" | "edit";

interface UserFormProps {
	open: boolean;
	mode: Mode;
	initial?: User | null;
	onClose: () => void;
	onSaved: () => void; // refrescar lista al guardar
}

const ROLES: UserRole[] = ["administrador", "profesor", "estudiante"];

export default function UserForm({ open, mode, initial, onClose, onSaved }: UserFormProps) {
	const [form, setForm] = useState<UserPayload>({
		username: "",
		email: "",
		rol: "profesor",
		nombre_completo: "",
		avatar_url: null,
		activo: true,
		password: "",
	});
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState<string | null>(null);

	useEffect(() => {
		if (mode === "edit" && initial) {
			setForm({
				username: initial.username,
				email: initial.email,
				rol: initial.rol,
				nombre_completo: initial.nombre_completo,
				avatar_url: initial.avatar_url,
				activo: initial.activo,
			});
		} else {
			setForm({ 
				username: "", 
				email: "", 
				rol: "profesor", 
				nombre_completo: "",
				avatar_url: null,
				activo: true, 
				password: "" 
			});
		}
		setError(null);
	}, [mode, initial, open]);

		const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
			const target = e.target;
			const name = target.name as keyof UserPayload;
			const value = (target as HTMLInputElement).type === "checkbox"
				? (target as HTMLInputElement).checked
				: target.value;
				setForm((prev) => ({
					...prev,
					[name]: value as UserPayload[typeof name],
				}));
		};

	const handleSubmit = async (e: React.FormEvent) => {
		e.preventDefault();
		setLoading(true);
		setError(null);
		try {
			if (mode === "create") {
				const payload: UserPayload = { ...form };
				if (!payload.password || payload.password.trim() === "") {
					setError("La contraseña es requerida para crear un nuevo usuario");
					setLoading(false);
					return;
				}
				await API.post("/users/", payload);
			} else if (mode === "edit" && initial) {
				const payload: Partial<UserPayload> = { ...form };
				// Solo enviar contraseña si no está vacía
				if (!payload.password || payload.password.trim() === "") {
					delete payload.password;
				}
				await API.put(`/users/${initial.id}/`, payload);
			}
			onSaved();
			onClose();
		} catch (err) {
			const axiosErr = err as { response?: { data?: unknown } };
			const msg = axiosErr.response?.data ? JSON.stringify(axiosErr.response.data) : "Error al guardar usuario";
			setError(msg);
		} finally {
			setLoading(false);
		}
	};

	if (!open) return null;

	return (
		<div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 p-4">
			<div className="w-full max-w-lg rounded-lg bg-white p-6 shadow-lg">
				<div className="mb-4 flex items-center justify-between">
					<h2 className="text-xl font-semibold text-gray-800">
						{mode === "create" ? "Nuevo usuario" : "Editar usuario"}
					</h2>
					<button onClick={onClose} className="text-gray-500 hover:text-gray-700">✕</button>
				</div>

				{error && (
					<div className="mb-4 rounded border border-red-200 bg-red-50 p-3 text-red-700 text-sm">{error}</div>
				)}

				<form className="space-y-4" onSubmit={handleSubmit}>
					<div>
						<label className="mb-1 block text-sm font-medium text-gray-700">Nombre completo</label>
						<input
							name="nombre_completo"
							value={form.nombre_completo}
							onChange={handleChange}
							className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
							required
						/>
					</div>
					<div>
						<label className="mb-1 block text-sm font-medium text-gray-700">Nombre de usuario</label>
						<input
							name="username"
							value={form.username}
							onChange={handleChange}
							className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
							required
						/>
					</div>
					<div>
						<label className="mb-1 block text-sm font-medium text-gray-700">Correo</label>
						<input
							type="email"
							name="email"
							value={form.email}
							onChange={handleChange}
							className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
							required
						/>
					</div>
					<div className="grid grid-cols-1 gap-4 md:grid-cols-2">
						<div>
							<label className="mb-1 block text-sm font-medium text-gray-700">Rol</label>
							<select
								name="rol"
								value={form.rol}
								onChange={handleChange}
								className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
								required
							>
								{ROLES.map((r) => (
									<option key={r} value={r}>
										{r === 'administrador' ? 'Administrador' : r === 'profesor' ? 'Profesor' : 'Estudiante'}
									</option>
								))}
							</select>
						</div>
						<div className="flex items-center gap-2 pt-6">
							<input type="checkbox" name="activo" checked={form.activo} onChange={handleChange} />
							<label className="text-sm text-gray-700">Activo</label>
						</div>
					</div>

					<div>
						<label className="mb-1 block text-sm font-medium text-gray-700">
							Contraseña {mode === "edit" && <span className="text-gray-400">(dejar en blanco para no cambiar)</span>}
						</label>
						<input
							type="password"
							name="password"
							value={form.password ?? ""}
							onChange={handleChange}
							className="w-full rounded border border-gray-300 px-3 py-2 focus:border-blue-500 focus:outline-none"
							placeholder={mode === "edit" ? "••••••••" : ""}
							{...(mode === "create" ? { required: true } : {})}
						/>
					</div>

					<div className="mt-6 flex items-center justify-end gap-3">
						<button type="button" onClick={onClose} className="rounded border px-4 py-2 text-gray-700 hover:bg-gray-50">Cancelar</button>
						<button
							type="submit"
							disabled={loading}
							className="rounded bg-blue-600 px-4 py-2 font-semibold text-white hover:bg-blue-700 disabled:opacity-60"
						>
							{loading ? "Guardando..." : mode === "create" ? "Crear" : "Guardar"}
						</button>
					</div>
				</form>
			</div>
		</div>
	);
}