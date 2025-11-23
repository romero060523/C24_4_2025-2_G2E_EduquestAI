// src/layouts/EstudianteLayout.tsx
import React from "react";
import SidebarEstudiante from "../components/estudiante/SidebarEstudiante";
import { Outlet } from "react-router-dom";
import ChatWidget from "../components/ChatWidget";
import { useAuth } from "../hooks/useAuth";

const EstudianteLayout: React.FC = () => {
  const { usuario } = useAuth();

  return (
    <div className="flex min-h-screen bg-gray-100">
      <SidebarEstudiante />
      <main className="flex-1 p-8">
        <Outlet />
      </main>
      {usuario && (
        <ChatWidget userId={usuario.id} userRole={usuario.rol} />
      )}
    </div>
  );
};

export default EstudianteLayout;
