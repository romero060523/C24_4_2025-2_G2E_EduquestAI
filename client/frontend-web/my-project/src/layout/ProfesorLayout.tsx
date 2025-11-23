// src/layouts/ProfesorLayout.tsx
import React from "react";
import SidebarProfesor from "../components/profesor/SidebarProfesor";
import { Outlet } from "react-router-dom";
import ChatWidget from "../components/ChatWidget";
import { useAuth } from "../hooks/useAuth";

const ProfesorLayout: React.FC = () => {
  const { usuario } = useAuth();

  return (
    <div className="flex min-h-screen bg-gray-100">
      <SidebarProfesor />
      <main className="flex-1 p-8">
        {/* El componente cargado dinámicamente */}
        <Outlet />
      </main>
      {usuario && (
        <ChatWidget userId={usuario.id} userRole={usuario.rol} />
      )}
    </div>
  );
};

export default ProfesorLayout;
