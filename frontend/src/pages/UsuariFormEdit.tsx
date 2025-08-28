import React, { useEffect } from "react";
import { useForm, SubmitHandler, Controller } from "react-hook-form";
import { useNavigate, useParams } from "react-router-dom";
import {
  Paper,
  TextInput,
  Button,
  Flex,
  Group,
  Title,
  Loader,
  Text,
  Select,
} from "@mantine/core";
import { showNotification } from "@mantine/notifications";
import { IconCheck, IconX, IconChevronDown } from "@tabler/icons-react";
import {
  useUsuari,
  useUpdateUsuari,
  resetPasswordUsuari,
} from "../api/usuaris";

type FormValues = {
  nom: string;
  email: string;
  rol: string;
  contrasenya: string; // Campo para la contraseña
};

export default function UsuariFormEdit() {
  const { id } = useParams<{ id: string }>();
  const usuariId = Number(id);
  const navigate = useNavigate();

  const { data: item, isLoading, error } = useUsuari(usuariId);
  const updateMutation = useUpdateUsuari();

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<FormValues>();

  useEffect(() => {
    if (item) {
      reset({
        nom: item.nom,
        email: item.email,
        rol: item.rol,
        contrasenya: "",
      });
    }
  }, [item, reset]);

  const onSubmit: SubmitHandler<FormValues> = (data) => {
    const payload = {
      nom: data.nom,
      email: data.email,
      rol: data.rol,
      contrasenya: data.contrasenya,
      contrasenyaTemporal: false, // 🔥 Afegeix aquest camp
    };

    updateMutation.mutate(
      { id: usuariId, data: payload },
      {
        onSuccess: () => {
          showNotification({
            title: "Correcte",
            message: "Usuari actualitzat correctament",
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
      }
    );
  };

  if (isLoading || !item) return <Loader />;
  if (error) return <Text color="red">Error: {error.message}</Text>;

  return (
    <Paper
      p="lg"
      radius="md"
      shadow="sm"
      style={{
        maxWidth: 600,
        margin: "auto",
        fontFamily: "Poppins, sans-serif",
      }}
    >
      <Title order={3} mb="md">
        Editar Usuari #{usuariId}
      </Title>

      <Button
        size="xs"
        color="red"
        variant="light"
        onClick={async () => {
          try {
            const novaContrasenya = await resetPasswordUsuari(usuariId);
            showNotification({
              title: "Contrasenya restablerta",
              message: `Nova contrasenya: ${novaContrasenya}`,
              icon: <IconCheck size={16} />,
              color: "teal",
            });
          } catch (err: unknown) {
            const msg = err instanceof Error ? err.message : "Error desconegut";
            showNotification({
              title: "Error",
              message: msg,
              icon: <IconX size={16} />,
              color: "red",
            });
          }
        }}
      >
        Reset Password
      </Button>

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
          <TextInput
            size="sm"
            label="Contrasenya"
            placeholder="Deixa en blanc si no vols canviar-la"
            type="password"
            {...register("contrasenya")}
            error={errors.contrasenya?.message}
          />

          <div style={{ marginTop: "24px" }}>
            <Group justify="apart">
              <Button
                size="sm"
                type="submit"
                loading={updateMutation.status === "pending"}
              >
                Desa
              </Button>
              <Button
                size="sm"
                variant="outline"
                onClick={() => navigate("/usuaris")}
              >
                Cancel·la
              </Button>
            </Group>
          </div>
        </Flex>
      </form>
    </Paper>
  );
}
