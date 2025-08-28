import React from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate } from "react-router-dom";
import {
  Paper,
  TextInput,
  Button,
  Flex,
  Group,
  Title,
  Select,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX, IconChevronDown } from "@tabler/icons-react";
import { useCreateUsuari } from "../api/usuaris";

type FormValues = {
  nom: string;
  email: string;
  rol: string;
};

export default function UsuariFormNew() {
  const navigate = useNavigate();
  const createMutation = useCreateUsuari();

  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<FormValues>({
    defaultValues: {
      nom: "",
      email: "",
      rol: "",
    },
  });

  const onSubmit: SubmitHandler<FormValues> = (data) => {
  const payload = {
    nom: data.nom,
    email: data.email,
    rol: data.rol,
    contrasenya: "123", // Replace with appropriate logic for password
  };

  console.log("Payload enviat:", payload); // Verifica el payload

  createMutation.mutate(payload, {
    onSuccess: () => {
      showNotification({
        title: "Correcte",
        message: "Usuari creat correctament",
        icon: <IconCheck size={18} />,
        color: "teal",
      });
      navigate("/usuaris");
    },
    onError: (err: unknown) => {
      const msg = err instanceof Error ? err.message : String(err);
      showNotification({
        title: "Error",
        message: msg,
        icon: <IconX size={18} />,
        color: "red",
      });
    },
  });
};

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{ maxWidth: 600, margin: "auto", fontFamily: "Poppins, sans-serif" }}
    >
      <Title order={3} mb="md">
        Nou Usuari
      </Title>
      <form onSubmit={handleSubmit(onSubmit)}>
        <Flex direction="column" gap="md">
          <TextInput
            size="sm"
            label="Nom"
            placeholder="Nom de l'usuari"
            {...register("nom", {
              required: "El nom és obligatori",
            })}
            error={errors.nom?.message}
          />
          <TextInput
            size="sm"
            label="Email"
            placeholder="Email de l'usuari"
            {...register("email", {
              required: "L'email és obligatori",
              pattern: {
                value: /^[^\s@]+@[^\s@]+\.[^\s@]+$/,
                message: "L'email no és vàlid",
              },
            })}
            error={errors.email?.message}
          />
<Controller
  name="rol"
  control={control}
  rules={{ required: "El rol és obligatori" }}
  render={({ field }) => (
    <Select
      size="sm"
      label="Rol"
      placeholder="Selecciona un rol"
      data={[
        { value: "ADMINISTRADOR", label: "Administrador" },
        { value: "GESTOR", label: "Gestor" },
        { value: "TREBALLADOR", label: "Treballador" },
      ]}
      value={field.value}
      onChange={field.onChange}
      error={errors.rol?.message}
      rightSection={<IconChevronDown size={14} />}
    />
  )}
/>

          <Group justify="apart" mt="xl">
            <Button
              size="sm"
              type="submit"
              loading={createMutation.status === "pending"}
            >
              Crea
            </Button>
            <Button
              size="sm"
              variant="outline"
              onClick={() => navigate("/usuaris")}
            >
              Cancel·la
            </Button>
          </Group>
        </Flex>
      </form>
    </Paper>
  );
}