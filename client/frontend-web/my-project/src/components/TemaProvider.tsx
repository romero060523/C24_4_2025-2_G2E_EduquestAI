import { useTema } from "../hooks/useTema";

export default function TemaProvider({ children }: { children: React.ReactNode }) {
  useTema();
  return <>{children}</>;
}

